package ru.skuptsov.sphinx.console.coordinator.exception;

public class SqlApplicationException extends RuntimeException {

    private String description;

    public SqlApplicationException() {
    }

    public SqlApplicationException(String message) {
        super(message);
    }

    public SqlApplicationException(Throwable cause, String description) {
        super(cause);
        this.description = description;
    }

    public SqlApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlApplicationException(Throwable cause) {
        super(cause);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



