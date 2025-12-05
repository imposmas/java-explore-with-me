package ru.practicum.ewm.dto.comment;

import lombok.*;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateCommentDto {
    private Long id;
    private Long eventId;
    private String text;
    private UserShortDto author;
    private LocalDateTime createdOn;
}
