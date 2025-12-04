package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.request.EventParticipantsService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
public class PrivateEventParticipantsController {

    private final EventParticipantsService eventParticipantsService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        List<ParticipationRequestDto> result =
                eventParticipantsService.getEventParticipants(userId, eventId);

        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> updateStatuses(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest dto
    ) {
        EventRequestStatusUpdateResult updated =
                eventParticipantsService.updateStatuses(userId, eventId, dto);

        return ResponseEntity.ok(updated);
    }
}