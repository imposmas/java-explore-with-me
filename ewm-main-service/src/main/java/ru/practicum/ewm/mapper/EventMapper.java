package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.common.util.DateTimeUtils;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    public Event toEntity(NewEventDto dto, User initiator, Category category, Location location) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .title(dto.getTitle())
                .eventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER))
                .category(category)
                .location(location)
                .initiator(initiator)
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .state(EventState.PENDING)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public EventShortDto toShortDto(Event e, long confirmedRequests, long views) {
        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .title(e.getTitle())
                .eventDate(e.getEventDate().format(DateTimeUtils.FORMATTER))
                .category(categoryMapper.toDto(e.getCategory()))
                .initiator(userMapper.toShort(e.getInitiator()))
                .paid(e.getPaid())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
    }

    public EventFullDto toFullDto(Event e, long confirmedRequests, long views) {
        return EventFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .description(e.getDescription())
                .title(e.getTitle())
                .eventDate(e.getEventDate().format(DateTimeUtils.FORMATTER))
                .createdOn(e.getCreatedOn().format(DateTimeUtils.FORMATTER))
                .publishedOn(e.getPublishedOn() != null ?
                        e.getPublishedOn().format(DateTimeUtils.FORMATTER) : null)
                .category(categoryMapper.toDto(e.getCategory()))
                .initiator(userMapper.toShort(e.getInitiator()))
                .location(locationMapper.toDto(e.getLocation()))
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .state(e.getState())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
    }

    public void updateEventByUser(Event event,
                                  UpdateEventUserRequest dto,
                                  Category category,
                                  Location location) {

        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (category != null) event.setCategory(category);
        if (location != null) event.setLocation(location);

        if (dto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER));
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }
    }

    public void updateEventByAdmin(Event event,
                                   UpdateEventAdminRequest dto,
                                   Category category,
                                   Location location) {

        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (category != null) event.setCategory(category);
        if (location != null) event.setLocation(location);

        if (dto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeUtils.FORMATTER));
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT -> event.setState(EventState.PUBLISHED);
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
            }
        }
    }
}