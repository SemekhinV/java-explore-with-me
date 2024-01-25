package ru.practicum.ewm.event.enums;

import java.util.Optional;

public enum EventStateActionAdmin {

    PUBLISH_EVENT,
    REJECT_EVENT;

    public static Optional<EventStateActionAdmin> from(String stringState) {

        for (var state : values()) {

            if (state.name().equalsIgnoreCase(stringState)) {

                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
