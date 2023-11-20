package ru.practicum.ewm.error.exception.request;

public class RequestExistException extends RuntimeException {

    public RequestExistException(String message) {
        super(message);
    }
}
