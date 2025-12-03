package ru.practicum.ewm.dto.event;

import lombok.*;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.dto.location.LocationDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    private String annotation;
    private String description;
    private CategoryDto category;
    private Boolean paid;
    private String title;
    private Long confirmedRequests;
    private String eventDate;
    private String createdOn;
    private String publishedOn;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState state;
    private UserShortDto initiator;
    private LocationDto location;
    private Long views;
}