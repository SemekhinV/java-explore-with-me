package ru.practicum.ewm.error.exception.event;

public class EventNotExistException extends RuntimeException {

    public EventNotExistException(String message) {
        super(message);
    }
}
