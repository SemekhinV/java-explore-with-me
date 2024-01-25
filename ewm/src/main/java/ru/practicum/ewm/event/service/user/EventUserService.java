package ru.practicum.ewm.event.service.user;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.enums.EventSortState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventUserService {

    EventFullDto getEventById(Long id, HttpServletRequest request);

    List<EventFullDto> getWithParametersByUser(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean available,
                                               EventSortState sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request);
}