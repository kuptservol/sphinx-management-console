package ru.skuptsov.sphinx.console.coordinator.exception;

public class SearchStringNotFoundException extends RuntimeException {

    public SearchStringNotFoundException() {
    }

    public SearchStringNotFoundException(String message) {
        super(message);
    }

    public SearchStringNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchStringNotFoundException(Throwable cause) {
        super(cause);
    }
}



