package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.AdminEventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventsController {

    private final AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> search(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<EventFullDto> result = adminEventService.getEvents(
                users, states, categories, rangeStart, rangeEnd, from, size
        );
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest dto
    ) {
        EventFullDto updated = adminEventService.updateEvent(eventId, dto);
        return ResponseEntity.ok(updated);
    }
}