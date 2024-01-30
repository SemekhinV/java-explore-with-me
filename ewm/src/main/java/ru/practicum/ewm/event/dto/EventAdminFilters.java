package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventAdminFilters {

    private Integer size;

    private Integer from;

    private List<Long> categories;

    private List<EventState> states;

    private LocalDateTime rangeStart;

    private List<Long> users;

    private LocalDateTime rangeEnd;
}
