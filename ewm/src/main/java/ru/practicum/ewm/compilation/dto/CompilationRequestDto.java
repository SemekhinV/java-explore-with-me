package ru.practicum.ewm.compilation.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRequestDto {

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    private List<Long> events = new ArrayList<>();
}
