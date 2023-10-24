package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetWithParametersDto {

    private String text;

    private List<Long> users;

    private EventState states;

    private List<Long> categories;

    private Boolean paid;

    private String rangeStart;

    private LocalDateTime startDate;

    private String rangeEnd;

    private LocalDateTime endDate;

    private Boolean available;

    private SortState sort;

    private Integer from;

    private Integer size;
}
