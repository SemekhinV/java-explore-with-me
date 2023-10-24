package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.enums.EventState;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;

    @Size(max = 1000)
    private String annotation;

    private CategoryResponseDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdOn;

    @Size(max = 100)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private ShortEvenDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    @Size(max = 100)
    private String title;

    private Long views;
}