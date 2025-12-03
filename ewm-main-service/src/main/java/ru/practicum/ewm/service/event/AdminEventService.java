package ru.practicum.ewm.service.event;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEvents(List<Long> users,
                                 List<String> states,
                                 List<Long> categories,
                                 String rangeStart,
                                 String rangeEnd,
                                 int from,
                                 int size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto);
}
