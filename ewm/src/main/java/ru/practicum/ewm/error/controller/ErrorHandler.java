package ru.practicum.ewm.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.error.exception.*;
import ru.practicum.ewm.error.response.Error;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error entityAlreadyExist(final EntityExistException e) {

        return Error.builder()
                .status("BAD_REQUEST")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error entityAlreadyExist(final CategoryNotEmpyException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error eventPublishException(final EventPublishException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error eventStateException(final EventStateException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error eventTimeException(final EventTimeException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error participantLimitException(final ParticipantLimitException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error requestStateException(final RequestStateException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error userAccessException(final UserAccessException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
