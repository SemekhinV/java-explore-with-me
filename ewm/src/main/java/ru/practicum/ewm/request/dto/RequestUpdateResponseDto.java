package ru.practicum.ewm.request.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateResponseDto {

    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;
}
