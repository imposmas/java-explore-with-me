package ru.practicum.ewm.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.ParticipationRequestRepository;
import ru.practicum.ewm.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    /**
     * Returns all participation requests created by the specified user.
     */
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Fetching participation requests: userId={}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User {} not found when fetching requests", userId);
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        List<ParticipationRequestDto> result = requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toDto)
                .toList();

        log.debug("Found {} participation requests for user {}", result.size(), userId);
        return result;
    }

    /**
     * Creates a participation request for the given event by the given user.
     * Applies all event constraints (state, duplicates, limits).
     */
    @Transactional
    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info("Creating participation request: userId={}, eventId={}", userId, eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new NotFoundException("User with id=" + userId + " was not found");
                });

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        log.debug("Confirmed requests for event {}: {}", eventId, confirmed);

        if (event.getParticipantLimit() > 0 && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
        }

        RequestStatus status = event.getRequestModeration() && event.getParticipantLimit() != 0
                ? RequestStatus.PENDING
                : RequestStatus.CONFIRMED;

        log.debug("Determined request status: {}", status);

        ParticipationRequest request = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .status(status)
                .created(LocalDateTime.now())
                .build();

        ParticipationRequest saved = requestRepository.save(request);
        log.info("Participation request created: id={}", saved.getId());

        return requestMapper.toDto(saved);
    }

    /**
     * Cancels the request created by the user.
     */
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Cancelling request: userId={}, requestId={}", userId, requestId);

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    return new NotFoundException("Request with id=" + requestId + " was not found");
                });

        if (!request.getRequester().getId().equals(userId)) {
            log.error("User {} attempted to cancel request {} belonging to another user",
                    userId, requestId);
            throw new ConflictException("User cannot cancel others' requests");
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest saved = requestRepository.save(request);

        log.info("Request {} cancelled by user {}", requestId, userId);
        return requestMapper.toDto(saved);
    }
}