package ru.practicum.ewm.error.exception;

public class EventPublishException extends RuntimeException {

    public EventPublishException(String message) {
        super(message);
    }
}
