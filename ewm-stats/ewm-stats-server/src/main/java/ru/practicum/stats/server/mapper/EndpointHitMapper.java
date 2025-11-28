package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

public class EndpointHitMapper {

    public static EndpointHit toEntity(EndpointHitDto dto) {
        if (dto == null)
            return null;

        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}