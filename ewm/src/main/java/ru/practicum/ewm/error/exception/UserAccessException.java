package ru.practicum.ewm.error.exception;

public class UserAccessException extends RuntimeException {

    public UserAccessException(String message) {
        super(message);
    }
}
