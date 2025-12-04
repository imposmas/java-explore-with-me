package ru.practicum.ewm.dto.request;

import lombok.*;
import ru.practicum.ewm.constants.RequestStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private String created;
    private RequestStatus status;
}
