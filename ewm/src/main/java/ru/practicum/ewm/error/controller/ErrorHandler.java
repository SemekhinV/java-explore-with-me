package ru.practicum.ewm.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.error.exception.category.CategoryDataException;
import ru.practicum.ewm.error.exception.category.CategoryExistsException;
import ru.practicum.ewm.error.exception.category.CategoryNotEmpyException;
import ru.practicum.ewm.error.exception.event.*;
import ru.practicum.ewm.error.exception.request.RequestExistException;
import ru.practicum.ewm.error.exception.request.RequestStateException;
import ru.practicum.ewm.error.exception.user.UserAccessException;
import ru.practicum.ewm.error.exception.user.UserExistException;
import ru.practicum.ewm.error.exception.util.EntityExistException;
import ru.practicum.ewm.error.exception.util.TimeIntervalException;
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
                .status("NOT_FOUND")
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
    public Error categoryAlreadyExist(final CategoryExistsException e) {

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
    public Error categoryDataException(final CategoryDataException e) {

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
    public Error eventNotPublishException(final EventNotPublishedException e) {

        return Error.builder()
                .status("BAD_REQUEST")
                .reason("Event should be published.")
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error eventTimeException(final TimeIntervalException e) {

        return Error.builder()
                .status("BAD_REQUEST")
                .reason("Exclusion of time interval boundary.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error participantLimitException(final EventParticipantLimitException e) {

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

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error userExistException(final UserExistException e) {

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
    public Error requestExistException(final RequestExistException e) {

        return Error.builder()
                .status("CONFLICT")
                .reason("Incorrect request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error requestExistException(final EventNotExistException e) {

        return Error.builder()
                .status("NOT_FOUND")
                .reason("Event does`t exist.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
