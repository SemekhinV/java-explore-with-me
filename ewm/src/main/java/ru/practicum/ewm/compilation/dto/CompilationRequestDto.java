package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRequestDto {

    @NotBlank
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
