package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.event.entity.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

    @NotNull
    @Size(min = 3, max = 1000)
    private String annotation;

    @NotNull
    @Size(min = 3, max = 100)
    private String title;

    @NotNull
    private Long category;

    @NotNull
    @Size(min = 20, max = 1000)
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;
}
