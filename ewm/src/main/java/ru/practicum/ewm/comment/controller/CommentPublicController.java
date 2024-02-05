package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.user.CommentUserService;

import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.COMMENT_REQUEST;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/events/")
public class CommentPublicController {

    private final CommentUserService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{eventId}/comments")
    public List<CommentResponseDto> getCommentsByEventId(@PathVariable long eventId) {

        log.info(COMMENT_REQUEST, "get by event id");

        return service.getCommentsByEventId(eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/comments/{commentId}")
    public CommentResponseDto getCommentById(@PathVariable long commentId) {

        log.info(COMMENT_REQUEST, "get by id");

        return service.getCommentById(commentId);
    }
}
