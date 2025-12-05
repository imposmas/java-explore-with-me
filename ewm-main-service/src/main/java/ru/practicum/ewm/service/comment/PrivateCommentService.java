package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.PrivateCommentDto;

import java.util.List;

public interface PrivateCommentService {

    PrivateCommentDto createComment(Long userId, Long eventId, CommentDto dto);

    List<PrivateCommentDto> getCommentsForEvent(
            Long userId,
            Long eventId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    );

    PrivateCommentDto getComment(Long userId, Long commentId);

    PrivateCommentDto updateComment(Long userId, Long commentId, CommentDto dto);

    void deleteComment(Long userId, Long commentId);
}
