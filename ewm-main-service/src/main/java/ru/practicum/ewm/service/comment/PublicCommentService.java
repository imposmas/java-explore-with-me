package ru.practicum.ewm.service.comment;


import ru.practicum.ewm.dto.comment.PublicCommentDto;

import java.util.List;

public interface PublicCommentService {

    List<PublicCommentDto> getCommentsForEvent(
            Long eventId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    );
}
