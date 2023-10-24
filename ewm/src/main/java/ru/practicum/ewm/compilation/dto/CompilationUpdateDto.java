package ru.practicum.ewm.compilation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateDto {

    private String title;

    private Boolean pinned;

    private List<Long> events;
}
