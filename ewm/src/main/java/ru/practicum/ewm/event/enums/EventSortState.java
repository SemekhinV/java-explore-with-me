package ru.practicum.ewm.event.enums;

import java.util.Optional;

public enum EventSortState {

    EVENT_DATE,
    VIEWS;

    public static Optional<EventSortState> from(String stringState) {

        for (var state : values()) {

            if (state.name().equalsIgnoreCase(stringState)) {

                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
