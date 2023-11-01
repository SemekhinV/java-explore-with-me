package ru.practicum.ewm.error.exception;

public class EventNotPublishedException extends RuntimeException {

    public EventNotPublishedException(String message) {
        super(message);
    }
}
