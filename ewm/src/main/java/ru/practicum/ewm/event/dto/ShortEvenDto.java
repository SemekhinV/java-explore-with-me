package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.user.dto.ShortUserDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShortEvenDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private ShortUserDto initiator;

    private Long confirmedRequests;

    private CategoryResponseDto category;

    @Size(max = 2000)
    private String annotation;

    private Boolean paid;

    @Size(max = 120)
    private String title;

    private Long views;
}
