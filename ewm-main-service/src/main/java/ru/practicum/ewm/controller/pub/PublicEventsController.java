package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.service.event.PublicEventService;
import ru.practicum.ewm.service.statistics.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private final PublicEventService publicEventService;
    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        statisticsService.saveHit(request);
        List<EventShortDto> events = publicEventService.getEvents(
                text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size
        );

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        statisticsService.saveHit(request);
        EventFullDto event = publicEventService.getEvent(id);
        return ResponseEntity.ok(event);
    }
}