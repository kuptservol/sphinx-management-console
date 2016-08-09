package ru.skuptsov.sphinx.console.coordinator.exception;

public class ChainException extends RuntimeException {

    public ChainException() {
    }

    public ChainException(String message) {
        super(message);
    }

    public ChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChainException(Throwable cause) {
        super(cause);
    }
}



