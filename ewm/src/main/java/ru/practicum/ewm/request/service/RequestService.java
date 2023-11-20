package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestUpdateDto;
import ru.practicum.ewm.request.dto.RequestUpdateResponseDto;

import java.util.List;

public interface RequestService {

    RequestDto save(Long userId, Long eventId);

    List<RequestDto> get(Long userId);

    List<RequestDto> getByOwnerOfEvent(Long userId, Long eventId);

    RequestUpdateResponseDto updateRequest(Long userId, Long eventId, RequestUpdateDto requestUpdateDto);

    RequestDto rejectRequest(Long userId, Long requestId);
}
