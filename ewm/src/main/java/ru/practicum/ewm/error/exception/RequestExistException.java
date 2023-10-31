package ru.practicum.ewm.error.exception;

public class RequestExistException extends RuntimeException {

    public RequestExistException(String message) {
        super(message);
    }
}
