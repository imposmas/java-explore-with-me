package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHitDto {

    private Long id;

    @NotBlank(message = "app must not be blank")
    private String app;

    @NotBlank(message = "uri must not be blank")
    private String uri;

    @NotBlank(message = "ip must not be blank")
    private String ip;

    @NotNull(message = "timestamp is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}