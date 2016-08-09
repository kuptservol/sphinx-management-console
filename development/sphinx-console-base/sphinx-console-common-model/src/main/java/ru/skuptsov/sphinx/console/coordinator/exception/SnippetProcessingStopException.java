package ru.skuptsov.sphinx.console.coordinator.exception;

public class SnippetProcessingStopException extends RuntimeException {

    public SnippetProcessingStopException() {
    }

    public SnippetProcessingStopException(String message) {
        super(message);
    }

    public SnippetProcessingStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnippetProcessingStopException(Throwable cause) {
        super(cause);
    }

}
