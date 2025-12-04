package ru.practicum.ewm.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exceptions.BadRequestException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.DateTimeUtils;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.statistics.StatisticsService;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatisticsService statisticsService;

    /**
     * Returns public events filtered by text, categories, date range, payment,
     * availability and sorted by views/eventDate. Only PUBLISHED events.
     */
    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         int from,
                                         int size) {
        log.info("PUBLIC: Searching events with filters text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}",
                text, categories, paid, rangeStart, rangeEnd);

        LocalDateTime start = (rangeStart == null)
                ? LocalDateTime.now()
                : LocalDateTime.parse(rangeStart, DateTimeUtils.FORMATTER);

        LocalDateTime end = (rangeEnd == null)
                ? start.plusYears(100)
                : LocalDateTime.parse(rangeEnd, DateTimeUtils.FORMATTER);

        if (end.isBefore(start)) {
            throw new BadRequestException("rangeEnd cannot be before rangeStart");
        }

        Pageable pageable = PaginationUtils.toPageable(from, size);

        List<Event> events = eventRepository.searchPublicEvents(
                categories, paid, start, end, pageable
        );

        if (text != null && !text.isBlank()) {
            String t = text.toLowerCase();
            events = events.stream()
                    .filter(e ->
                            e.getAnnotation().toLowerCase().contains(t) ||
                                    e.getDescription().toLowerCase().contains(t)
                    )
                    .toList();
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 ||
                            requestRepository.countByEventIdAndStatus(
                                    e.getId(), RequestStatus.CONFIRMED
                            ) < e.getParticipantLimit())
                    .toList();
        }

        List<EventShortDto> result = events.stream()
                .map(e -> {
                    long confirmed = requestRepository.countByEventIdAndStatus(
                            e.getId(), RequestStatus.CONFIRMED);
                    long views = statisticsService.getViews(e.getId());
                    return eventMapper.toShortDto(e, confirmed, views);
                })
                .toList();

        if ("VIEWS".equals(sort)) {
            result = result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                    .toList();
        } else if ("EVENT_DATE".equals(sort)) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .toList();
        }

        return result;
    }

    /**
     * Returns a full public event. Only PUBLISHED events are accessible.
     */
    @Override
    public EventFullDto getEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " was not found");
        }

        long confirmed = requestRepository.countByEventIdAndStatus(
                id, RequestStatus.CONFIRMED);
        long views = statisticsService.getViews(id);

        return eventMapper.toFullDto(event, confirmed, views);
    }
}