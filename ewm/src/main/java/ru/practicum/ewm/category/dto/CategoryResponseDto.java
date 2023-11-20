package ru.practicum.ewm.category.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;

    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}
