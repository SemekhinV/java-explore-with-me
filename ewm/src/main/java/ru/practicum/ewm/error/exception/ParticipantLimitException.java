package ru.practicum.ewm.error.exception;

public class ParticipantLimitException extends RuntimeException {

    public ParticipantLimitException(String message) {
        super(message);
    }
}
