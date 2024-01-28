package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    @Mapping(target = "state", constant = "PENDING")
    Event toEvent(NewEventDto dto);

    EventFullDto toDto(Event event);

    EventShortDto toShortDto(Event event);

    List<EventFullDto> toDtoList(List<Event> events);

    List<EventShortDto> toShortDtoList(List<Event> events);
}
