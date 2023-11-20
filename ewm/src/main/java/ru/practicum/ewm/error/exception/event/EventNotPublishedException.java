package ru.practicum.ewm.error.exception.event;

public class EventNotPublishedException extends RuntimeException {

    public EventNotPublishedException(String message) {
        super(message);
    }
}
