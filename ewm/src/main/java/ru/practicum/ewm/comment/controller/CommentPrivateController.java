package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.local.CommentPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.ewm.util.EwmPatterns.COMMENT_REQUEST;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}")
public class CommentPrivateController {

    private final CommentPrivateService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/events/{eventId}/comments")
    public CommentResponseDto save(@RequestBody @Valid NewCommentDto newCommentDto,
                                            @PathVariable @NotNull Long eventId,
                                            @PathVariable @NotNull Long userId) {

        log.info(COMMENT_REQUEST, "create");

        return service.save(newCommentDto, eventId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/comments/{commentId}")
    public CommentResponseDto update(@RequestBody @Valid NewCommentDto newCommentDto,
                                     @PathVariable @NotNull Long commentId,
                                     @PathVariable @NotNull Long userId) {

        log.info(COMMENT_REQUEST, "update");

        newCommentDto.setId(commentId);

        newCommentDto.setUserId(userId);

        return service.update(newCommentDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public void delete(@PathVariable @NotNull Long commentId,
                       @PathVariable @NotNull Long userId,
                       @PathVariable @NotNull Long eventId) {

        log.info(COMMENT_REQUEST, "delete user comment");

        service.delete(commentId, userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events/{eventId}/comments")
    public List<CommentResponseDto> getAllUserCommentsToEvent(@PathVariable @NotNull Long userId,
                                                              @PathVariable @NotNull Long eventId) {

        log.info(COMMENT_REQUEST, "get all user comments");

        return service.getAllUserCommentsToEvent(userId, eventId);
    }
}
