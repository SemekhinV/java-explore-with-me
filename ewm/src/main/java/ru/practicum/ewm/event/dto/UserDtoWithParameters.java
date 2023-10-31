package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.SortState;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserDtoWithParameters {

    private String text;

    private Integer size;

    private Integer from;

    private Boolean available;

    private List<Long> categories;

    private String dateStart;

    private String dateEnd;

    private SortState sort;

    private Boolean paid;
}
