package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.comment.PublicCommentDto;
import ru.practicum.ewm.service.comment.PublicCommentService;

import java.util.List;


@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final PublicCommentService publicCommentService;

    @GetMapping
    public ResponseEntity<List<PublicCommentDto>> getComments(
            @RequestParam Long eventId,
            @RequestParam(required = false)
            String rangeStart,
            @RequestParam(required = false)
            String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PublicCommentDto> result = publicCommentService
                .getCommentsForEvent(eventId, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok(result);
    }
}