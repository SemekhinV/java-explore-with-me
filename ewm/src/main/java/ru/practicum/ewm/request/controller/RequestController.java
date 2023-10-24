package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto saveRequest(@RequestParam Long eventId,
                                  @PathVariable Long userId) {

        return service.save(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getCurrentUserRequests(@PathVariable Long userId) {

        return service.get(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long requestId,
                                    @PathVariable Long userId) {

        return service.rejectRequest(userId, requestId);
    }
}
