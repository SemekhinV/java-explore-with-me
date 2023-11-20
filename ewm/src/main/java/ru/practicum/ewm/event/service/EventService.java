package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortState;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventDto save(Long userId, EventRequestDto dro);

    EventDto updateByUser(Long userId, Long eventId, EventUpdateDto dto);

    EventDto updateByAdmin(Long eventId, AdminEventUpdateDto dto);

    List<ShortEvenDto> get(Long userId, Integer from, Integer size);

    EventDto getByUser(Long userId, Long eventId);

    EventDto get(Long id, HttpServletRequest request);

    List<EventDto> getWithParametersByAdmin(List<Long> users,
                                            EventState states,
                                            List<Long> categories,
                                            String rangeStart,
                                            String rangeEnd,
                                            Integer from,
                                            Integer size);

    List<EventDto> getWithParametersByUser(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           String rangeStart,
                                           String rangeEnd,
                                           Boolean available,
                                           SortState sort,
                                           Integer from,
                                           Integer size,
                                           HttpServletRequest request);
}