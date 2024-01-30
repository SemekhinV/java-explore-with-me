package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Поле аннотации не может быть пустым")
    @Length(min = 20, max = 2000, message = "Текст аннотации должен быть от 20 до 2000 символов")
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank(message = "Поле описания не может быть пустым")
    @Length(min = 20, max = 7000, message = "Текст описания должен быть от 20 до 7000 символов")
    private String description;

    @NotNull
    @FutureOrPresent(message = "Дата события должна быть в будущем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    private Long participantLimit = 0L;

    private Boolean requestModeration = true;

    @NotBlank(message = "Заголовок не может быть пустым")
    @Length(min = 3, max = 120, message = "Текст заголовка должен быть от 3 до 120 символов")
    private String title;
}
