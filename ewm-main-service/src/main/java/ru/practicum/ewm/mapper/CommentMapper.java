package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.PrivateCommentDto;
import ru.practicum.ewm.dto.comment.PublicCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.User;


@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public Comment toEntity(CommentDto dto, User author, Long eventId) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(author);
        comment.setEventId(eventId);
        return comment;
    }

    public PublicCommentDto toPublicDto(Comment c) {
        return PublicCommentDto.builder()
                .id(c.getId())
                .eventId(c.getEventId())
                .text(c.getText())
                .createdOn(c.getCreatedOn())
                .build();
    }

    public PrivateCommentDto toFullDto(Comment c) {
        return PrivateCommentDto.builder()
                .id(c.getId())
                .eventId(c.getEventId())
                .text(c.getText())
                .author(userMapper.toShort(c.getAuthor()))
                .createdOn(c.getCreatedOn())
                .build();
    }
}