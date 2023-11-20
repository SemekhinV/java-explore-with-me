package ru.practicum.ewm.error.exception.event;

public class EventPublishException extends RuntimeException {

    public EventPublishException(String message) {
        super(message);
    }
}
