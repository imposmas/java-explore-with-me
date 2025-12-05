package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.PrivateCommentDto;
import ru.practicum.ewm.service.comment.PrivateCommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final PrivateCommentService privateCommentService;

    @PostMapping
    public ResponseEntity<PrivateCommentDto> createComment(
            @PathVariable Long userId,
            @RequestParam Long eventId,
            @Valid @RequestBody CommentDto dto
    ) {
        PrivateCommentDto result = privateCommentService.createComment(userId, eventId, dto);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping
    public ResponseEntity<List<PrivateCommentDto>> getCommentsForEvent(
            @PathVariable Long userId,
            @RequestParam Long eventId,
            @RequestParam(required = false)
            String rangeStart,
            @RequestParam(required = false)
            String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PrivateCommentDto> result = privateCommentService
                .getCommentsForEvent(userId, eventId, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<PrivateCommentDto> getComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        PrivateCommentDto result = privateCommentService.getComment(userId, commentId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<PrivateCommentDto> updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto dto
    ) {
        PrivateCommentDto result = privateCommentService.updateComment(userId, commentId, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        privateCommentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}