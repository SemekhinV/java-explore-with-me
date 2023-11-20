package ru.practicum.ewm.request.dto;

import lombok.*;
import ru.practicum.ewm.request.enums.RequestOperationStatus;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateDto {

    private RequestOperationStatus status;

    private List<Long> requestIds;
}
