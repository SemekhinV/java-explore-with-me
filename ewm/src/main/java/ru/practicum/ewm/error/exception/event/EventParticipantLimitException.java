package ru.practicum.ewm.error.exception.event;

public class EventParticipantLimitException extends RuntimeException {

    public EventParticipantLimitException(String message) {
        super(message);
    }
}
