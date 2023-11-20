package ru.practicum.ewm.error.exception.user;

public class UserExistException extends RuntimeException {

    public UserExistException(String message) {
        super(message);
    }
}
