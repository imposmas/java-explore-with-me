package ru.practicum.ewm.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.ParticipationRequestRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipantsServiceImpl implements EventParticipantsService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    /**
     * Returns all participation requests for a specific event. Only the event initiator may access this data.
     *
     */
    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {

        log.info("Fetching participants for event {} by user {}", eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only event initiator can view participants");
        }

        List<ParticipationRequestDto> result = requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .toList();

        log.debug("Found {} participants for event {}", result.size(), eventId);

        return result;
    }

    /**
     * Updates statuses of participation requests for a specific event.
     * Only the event initiator can update request statuses.
     * Business rules:
     * - Only PENDING requests may be updated.
     * - If confirming a request exceeds the participant limit → 409.
     * - If the last available slot is filled, all remaining PENDING requests must be rejected.
     *
     */
    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatuses(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest dto) {

        log.info("Updating request statuses for event {} by user {}: {}", eventId, userId, dto);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only the initiator can update statuses");
        }

        List<ParticipationRequest> requests =
                requestRepository.findAllById(dto.getRequestIds());

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit();

        for (ParticipationRequest r : requests) {

            if (!r.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Request does not belong to this event");
            }

            if (r.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }

            if (dto.getStatus() == RequestStatus.CONFIRMED) {

                if (limit != 0 && confirmedCount >= limit) {
                    throw new ConflictException("Participant limit reached");
                }

                r.setStatus(RequestStatus.CONFIRMED);
                confirmed.add(requestMapper.toDto(requestRepository.save(r)));
                confirmedCount++;

                if (limit != 0 && confirmedCount == limit) {

                    log.info("Participant limit reached after confirming request {}. Rejecting all remaining pending requests.",
                            r.getId());

                    List<ParticipationRequest> pending =
                            requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);

                    for (ParticipationRequest p : pending) {
                        p.setStatus(RequestStatus.REJECTED);
                        rejected.add(requestMapper.toDto(requestRepository.save(p)));
                    }
                }

            } else if (dto.getStatus() == RequestStatus.REJECTED) {
                r.setStatus(RequestStatus.REJECTED);
                rejected.add(requestMapper.toDto(requestRepository.save(r)));
            }
        }

        log.info("Status update finished for event {}: confirmed={}, rejected={}",
                eventId, confirmed.size(), rejected.size());

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }
}