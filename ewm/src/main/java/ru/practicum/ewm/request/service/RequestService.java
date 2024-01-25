package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto save(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllUsersRequest(Long userId);

    ParticipationRequestDto rejectRequest(Long userId, Long requestId);
}
