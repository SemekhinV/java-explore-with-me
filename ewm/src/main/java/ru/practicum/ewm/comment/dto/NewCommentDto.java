package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    private Long id;

    private Long userId;

    @NotBlank
    @Size(max = 1000)
    private String text;
}
