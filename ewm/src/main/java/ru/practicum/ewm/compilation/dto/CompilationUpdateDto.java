package ru.practicum.ewm.compilation.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateDto {

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
