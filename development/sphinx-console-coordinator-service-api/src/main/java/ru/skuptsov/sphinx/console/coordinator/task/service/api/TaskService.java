package ru.skuptsov.sphinx.console.coordinator.task.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public interface TaskService<T extends Task> {
    Status execute(T t);
    Status handleAgentCallback(T task, Status status);
}
