package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.request.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestsController {

    private final PrivateRequestService privateRequestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                privateRequestService.getUserRequests(userId)
        );
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId
    ) {
        ParticipationRequestDto created =
                privateRequestService.addRequest(userId, eventId);

        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                privateRequestService.cancelRequest(userId, requestId)
        );
    }
}