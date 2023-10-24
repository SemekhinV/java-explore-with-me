package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.dto.GetWithParametersDto;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;

public interface EventRepository {

    List<Event> getByInitiator(GetWithParametersDto dto);

    List<Event> getByCategory(GetWithParametersDto dto);
}
