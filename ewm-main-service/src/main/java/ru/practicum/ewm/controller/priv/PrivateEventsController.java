package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.service.event.PrivateEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventsController {

    private final PrivateEventService privateEventService;

    @PostMapping
    public ResponseEntity<EventFullDto> create(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto dto
    ) {
        EventFullDto created = privateEventService.createEvent(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                privateEventService.getUserEvents(userId, from, size)
        );
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                privateEventService.getUserEvent(userId, eventId)
        );
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest dto
    ) {
        return ResponseEntity.ok(
                privateEventService.updateUserEvent(userId, eventId, dto)
        );
    }
}