package ru.practicum.ewm.event.service.user;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventPublicFilters;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventUserService {

    EventFullDto getEventById(Long id, HttpServletRequest request);

    List<EventFullDto> getWithParametersByUser(EventPublicFilters filters);
}