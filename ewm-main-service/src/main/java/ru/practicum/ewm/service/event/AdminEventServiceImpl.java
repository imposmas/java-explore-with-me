package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exceptions.BadRequestException;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.DateTimeUtils;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.constants.AdminStateAction;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.service.statistics.StatisticsService;
import ru.practicum.ewm.storage.CategoryRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.LocationRepository;
import ru.practicum.ewm.storage.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of admin event management operations.
 * Includes search, full event editing, publication and rejection.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository requestRepository;

    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final StatisticsService statisticsService;

    /**
     * Searches events with all admin filters.
     */
    @Override
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size) {

        log.info("ADMIN: Searching events with filters users={}, states={}, categories={}, rangeStart={}, rangeEnd={}",
                users, states, categories, rangeStart, rangeEnd);

        List<EventState> stateEnums = null;
        if (states != null && !states.isEmpty()) {
            try {
                stateEnums = states.stream()
                        .map(EventState::valueOf)
                        .toList();
            } catch (IllegalArgumentException ex) {
                log.error("Invalid event state in request: {}", states);
                throw new BadRequestException("Invalid event state");
            }
        }

        LocalDateTime start = (rangeStart == null) ? null :
                LocalDateTime.parse(rangeStart, DateTimeUtils.FORMATTER);

        LocalDateTime end = (rangeEnd == null) ? null :
                LocalDateTime.parse(rangeEnd, DateTimeUtils.FORMATTER);

        Pageable pageable = PaginationUtils.toPageable(from, size);

        List<Event> events = eventRepository.searchAdminEvents(
                users,
                stateEnums,
                categories,
                start,
                end,
                pageable
        );

        log.debug("ADMIN: event search result count: {}", events.size());

        return events.stream()
                .map(event -> {
                    long confirmed = requestRepository.countByEventIdAndStatus(
                            event.getId(),
                            RequestStatus.CONFIRMED
                    );
                    long views = statisticsService.getViews(event.getId());
                    return eventMapper.toFullDto(event, confirmed, views);
                })
                .toList();
    }

    /**
     * Updates event with full admin privileges.
     * Admin can change all fields, publish event or reject event.
     */
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {

        log.info("ADMIN: Updating event {}, request={}", eventId, dto);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event {} not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (dto.getEventDate() != null) {
            LocalDateTime newDate =
                    LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER);

            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                log.error("New event date {} violates the 2-hour rule", newDate);
                throw new BadRequestException("Event date must be at least 2 hours later than now");
            }

            if (event.getPublishedOn() != null &&
                    newDate.isBefore(event.getPublishedOn().plusHours(1))) {
                log.error("New date {} is earlier than 1 hour after publish", newDate);
                throw new BadRequestException("Event date must be at least 1 hour after publish date");
            }
        }

        if (dto.getStateAction() != null) {
            AdminStateAction action = dto.getStateAction();

            if (action == AdminStateAction.PUBLISH_EVENT &&
                    event.getState() != EventState.PENDING) {
                throw new ConflictException("Cannot publish event not in PENDING state");
            }

            if (action == AdminStateAction.REJECT_EVENT &&
                    event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Cannot reject already published event");
            }
        }

        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Category with id=" + dto.getCategory() + " was not found"
                    ));
        }

        Location location = null;
        if (dto.getLocation() != null) {
            location = locationMapper.toEntity(dto.getLocation());
            location = locationRepository.save(location);
        }

        eventMapper.updateEventByAdmin(event, dto, category, location);

        if (dto.getEventDate() != null) {
            event.setEventDate(
                    LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER)
            );
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    log.info("Event {} successfully published", eventId);
                }
                case REJECT_EVENT -> {
                    event.setState(EventState.CANCELED);
                    log.info("Event {} was rejected", eventId);
                }
            }
        }

        event = eventRepository.save(event);

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = statisticsService.getViews(eventId);

        return eventMapper.toFullDto(event, confirmed, views);
    }
}