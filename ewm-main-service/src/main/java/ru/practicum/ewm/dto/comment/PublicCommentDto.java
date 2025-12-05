package ru.practicum.ewm.dto.comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicCommentDto {
    private Long id;
    private Long eventId;
    private String text;
    private LocalDateTime createdOn;
}
