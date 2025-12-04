package ru.practicum.ewm.service.event;

import ru.practicum.ewm.dto.event.*;

import java.util.List;

public interface PrivateEventService {

    EventFullDto createEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto);
}