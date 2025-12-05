package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.dto.comment.PrivateCommentDto;

import java.util.List;

public interface AdminCommentService {

    List<PrivateCommentDto> getCommentsByEvent(
            Long eventId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    );

    List<PrivateCommentDto> getCommentsByUser(
            Long userId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    );

    void deleteComment(Long commentId);
}
