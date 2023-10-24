package ru.practicum.ewm.error.exception;

public class EntityExistException extends RuntimeException {

    public EntityExistException(String message) {
        super(message);
    }
}
