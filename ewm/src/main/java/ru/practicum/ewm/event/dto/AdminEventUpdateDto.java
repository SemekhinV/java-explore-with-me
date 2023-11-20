package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.AdminStateAction;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventUpdateDto extends EventUpdateDto{

    private AdminStateAction adminStateAction;
}
