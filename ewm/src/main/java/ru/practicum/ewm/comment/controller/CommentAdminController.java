package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.service.admin.CommentAdminService;

import static ru.practicum.ewm.util.EwmPatterns.COMMENT_REQUEST;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/admin/events/comments")
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {

        log.info(COMMENT_REQUEST, "admin delete comment");

        commentAdminService.delete(commentId);
    }
}
