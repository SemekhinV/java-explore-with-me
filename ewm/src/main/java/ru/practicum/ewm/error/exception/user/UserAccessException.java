package ru.practicum.ewm.error.exception.user;

public class UserAccessException extends RuntimeException {

    public UserAccessException(String message) {
        super(message);
    }
}
