package ru.practicum.ewm.event.service.admin;

import ru.practicum.ewm.event.dto.EventAdminFilters;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface EventAdminService {

    List<EventFullDto> getWithParametersByAdmin(EventAdminFilters filters);

    EventFullDto editEventByAdmin(UpdateEventAdminRequest dto);
}