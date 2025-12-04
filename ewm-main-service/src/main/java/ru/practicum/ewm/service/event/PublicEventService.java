package ru.practicum.ewm.service.event;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  String sort,
                                  int from,
                                  int size
    );

    EventFullDto getEvent(Long id);
}
