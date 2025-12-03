package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

import static ru.practicum.ewm.common.util.DateTimeUtils.FORMATTER;

@Component
public class RequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest r) {
        return ParticipationRequestDto.builder()
                .id(r.getId())
                .event(r.getEvent().getId())
                .requester(r.getRequester().getId())
                .status(r.getStatus())
                .created(r.getCreated().format(FORMATTER))
                .build();
    }
}
