package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.dto.AdminDtoWithParameters;
import ru.practicum.ewm.event.dto.UserDtoWithParameters;
import ru.practicum.ewm.event.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository {

    List<Event> admin(AdminDtoWithParameters dto, LocalDateTime start, LocalDateTime end);

    List<Event> user(UserDtoWithParameters dto, LocalDateTime start, LocalDateTime end);
}
