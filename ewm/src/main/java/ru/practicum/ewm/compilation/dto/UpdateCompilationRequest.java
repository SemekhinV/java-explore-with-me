package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {

    private Long id;

    @Size(min = 1, max = 50, message = "Размер заголовка должен составлять от 1 до 50 символов")
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
