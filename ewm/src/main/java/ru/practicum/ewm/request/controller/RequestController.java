package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.EVENT_REQUEST;
import static ru.practicum.ewm.util.EwmPatterns.PARTICIPATION_REQUEST;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable @NotNull Long userId) {

        log.info(PARTICIPATION_REQUEST, "get all user requests");

        return service.getAllUsersRequest(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@RequestParam @NotNull Long eventId,
                                               @PathVariable @NotNull Long userId) {

        log.info(EVENT_REQUEST, "create");

        return service.save(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @NotNull Long requestId,
                                                 @PathVariable @NotNull Long userId) {

        log.info(EVENT_REQUEST, "cancel request");

        return service.rejectRequest(userId, requestId);
    }
}
