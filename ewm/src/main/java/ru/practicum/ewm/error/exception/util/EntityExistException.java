package ru.practicum.ewm.error.exception.util;

public class EntityExistException extends RuntimeException {

    public EntityExistException(String message) {
        super(message);
    }
}
