package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Поле email - введено некорректное значение")
    @Size(min = 6, max = 254, message = "Некорректная длина email")
    private String email;

    @NotBlank(message = "Поле имени пользователя не может быть пустым")
    @Size(min = 2, max = 250, message = "Некорректная длина имени пользователя")
    private String name;
}
