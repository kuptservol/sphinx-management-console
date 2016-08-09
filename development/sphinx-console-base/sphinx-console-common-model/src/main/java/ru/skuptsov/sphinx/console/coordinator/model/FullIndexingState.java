package ru.skuptsov.sphinx.console.coordinator.model;

/**
 * Created by Andrey on 10.12.2014.
 */
public enum FullIndexingState {
    NOT_RUNNING,
    RUNNING,
    STOP,
    ERROR,
    READY_FOR_APPLY,
    IN_PROCESS,
    ERROR_APPLY,
    OK
}
