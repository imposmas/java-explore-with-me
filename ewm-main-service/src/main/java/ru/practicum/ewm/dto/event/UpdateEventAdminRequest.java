package ru.practicum.ewm.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.ewm.constants.AdminStateAction;
import ru.practicum.ewm.dto.location.LocationDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;
    private AdminStateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}