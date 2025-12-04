package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exceptions.BadRequestException;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.DateTimeUtils;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.service.statistics.StatisticsService;
import ru.practicum.ewm.storage.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository requestRepository;

    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final StatisticsService statisticsService;

    /**
     * Creates a new event owned by the specified user.
     */
    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        log.info("PRIVATE: createEvent userId={}, dto={}", userId, dto);

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        LocalDateTime eventDate =
                LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER);

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours later than now");
        }

        Location location = locationMapper.toEntity(dto.getLocation());
        location = locationRepository.save(location);

        Event event = eventMapper.toEntity(dto, initiator, category, location);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);

        event = eventRepository.save(event);

        log.info("Event {} successfully created", event.getId());
        return eventMapper.toFullDto(event, 0L, 0L);
    }

    /**
     * Returns all events created by the specified user.
     */
    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        log.debug("PRIVATE: getUserEvents userId={}, from={}, size={}", userId, from, size);

        Pageable pageable = PaginationUtils.toPageable(from, size);

        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(e -> {
                    long confirmed = requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED);
                    long views = statisticsService.getViews(e.getId());
                    return eventMapper.toShortDto(e, confirmed, views);
                })
                .toList();
    }

    /**
     * Returns a single event owned by the user.
     */
    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.debug("PRIVATE: getUserEvent userId={}, eventId={}", userId, eventId);

        Event event = getOwnedEvent(userId, eventId);

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = statisticsService.getViews(eventId);

        return eventMapper.toFullDto(event, confirmed, views);
    }

    /**
     * Updates an event owned by the user.
     */
    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("PRIVATE: updateUserEvent userId={}, eventId={}, dto={}", userId, eventId, dto);

        Event event = getOwnedEvent(userId, eventId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate =
                    LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER);
            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Event date must be at least 2 hours later than now");
            }
            event.setEventDate(newDate);
        }

        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
        }

        Location location = null;
        if (dto.getLocation() != null) {
            location = locationMapper.toEntity(dto.getLocation());
            location = locationRepository.save(location);
        }

        eventMapper.updateEventByUser(event, dto, category, location);

        event = eventRepository.save(event);

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = statisticsService.getViews(eventId);

        log.info("Event {} updated successfully", eventId);
        return eventMapper.toFullDto(event, confirmed, views);
    }

    /**
     * Returns the event if it belongs to the user, otherwise throws an exception.
     */
    private Event getOwnedEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User is not initiator of the event");
        }
        return event;
    }
}