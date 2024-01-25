package ru.practicum.ewm.event.enums;

import java.util.Optional;

public enum EventStateActionUser {

    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static Optional<EventStateActionUser> from(String stringState) {

        for (var state : values()) {

            if (state.name().equalsIgnoreCase(stringState)) {

                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
