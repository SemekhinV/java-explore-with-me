package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.event.dto.ShortEvenDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private Long id;

    private String title;

    private Boolean pinned;

    private List<ShortEvenDto> events;
}
