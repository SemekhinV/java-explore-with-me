package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.enums.EventState;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AdminDtoWithParameters {

    private Integer from;

    private Integer size;

    private List<Long> categories;

    private EventState states;

    private String rangeStart;

    private List<Long> users;

    private String rangeEnd;
}
