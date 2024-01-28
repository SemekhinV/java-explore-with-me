package ru.practicum.ewm.request.enums;

import java.util.Optional;

public enum RequestStatus {

    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED;

    public static Optional<RequestStatus> from(String stringState) {

        for (var state : values()) {

            if (state.name().equalsIgnoreCase(stringState)) {

                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
