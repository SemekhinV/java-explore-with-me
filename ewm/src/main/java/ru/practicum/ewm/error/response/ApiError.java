package ru.practicum.ewm.error.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class ApiError {

    private String status;

    private String reason;

    private String message;

    private String timestamp;
}
