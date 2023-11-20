package ru.practicum.ewm.error.exception.category;

public class CategoryExistsException extends RuntimeException {

    public CategoryExistsException(String message) {
        super(message);
    }
}
