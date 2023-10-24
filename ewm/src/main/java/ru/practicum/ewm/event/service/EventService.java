package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventDto save(Long userId, EventRequestDto dro);

    EventDto update(Long userId, Long eventId, EventUpdateDto dto);

    EventDto update(Long eventId, AdminEventUpdateDto dto);

    List<ShortEvenDto> get(Long userId, Integer from, Integer size);

    EventDto getByUser(Long userId, Long eventId);

    EventDto get(Long id, HttpServletRequest request);

    List<EventDto> getWithParametersByAdmin(GetWithParametersDto dto);

    List<EventDto> getWithParametersByUser(GetWithParametersDto dto, HttpServletRequest request);
}