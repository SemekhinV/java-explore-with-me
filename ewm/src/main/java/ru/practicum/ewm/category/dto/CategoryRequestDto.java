package ru.practicum.ewm.category.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class CategoryRequestDto {

    @NotBlank
    private String name;
}
