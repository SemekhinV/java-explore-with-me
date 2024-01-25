package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable @NotNull Long userId) {

        return service.getAllUsersRequest(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@RequestParam @NotNull Long eventId,
                                               @PathVariable @NotNull Long userId) {

        return service.save(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @NotNull Long requestId,
                                                 @PathVariable @NotNull Long userId) {

        return service.rejectRequest(userId, requestId);
    }
}
