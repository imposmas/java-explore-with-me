package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.PrivateCommentDto;
import ru.practicum.ewm.service.comment.AdminCommentService;

import java.util.List;


@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping("/by-event")
    public ResponseEntity<List<PrivateCommentDto>> getCommentsByEvent(
            @RequestParam Long eventId,
            @RequestParam(required = false)
            String rangeStart,
            @RequestParam(required = false)
            String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PrivateCommentDto> result = adminCommentService
                .getCommentsByEvent(eventId, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<PrivateCommentDto>> getCommentsByUser(
            @RequestParam Long userId,
            @RequestParam(required = false)
            String rangeStart,
            @RequestParam(required = false)
            String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PrivateCommentDto> result = adminCommentService
                .getCommentsByUser(userId, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId
    ) {
        adminCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}