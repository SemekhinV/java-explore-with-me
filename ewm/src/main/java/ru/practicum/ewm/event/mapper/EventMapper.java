package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.ShortEvenDto;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    Event toEvent(EventRequestDto dto);

    EventDto toDto(Event event);

    List<ShortEvenDto> toShortDtoList(List<Event> events);

    List<EventDto> toDtoList(List<Event> events);
}
