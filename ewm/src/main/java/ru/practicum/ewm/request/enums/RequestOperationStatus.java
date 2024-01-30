package ru.practicum.ewm.request.enums;

import java.util.Optional;

public enum RequestOperationStatus {

    CONFIRMED,
    REJECTED;

    public static Optional<RequestOperationStatus> from(String stringState) {

        for (var state : values()) {

            if (state.name().equalsIgnoreCase(stringState)) {

                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
