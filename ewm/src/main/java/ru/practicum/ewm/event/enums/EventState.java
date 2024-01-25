package ru.practicum.ewm.event.enums;

import java.util.Optional;

public enum EventState {
    PENDING,
    PUBLISHED,
    REJECTED,
    CANCELED;

    public static Optional<EventState> from(String stringState) {

        for (var state : values()) {

            if (state.name().equalsIgnoreCase(stringState)) {

                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
