package ru.skuptsov.sphinx.console.coordinator.task.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.Task;


public interface TaskServiceStrategyFactory<T extends Task> {
    TaskService<T> getTaskService(String taskName);
}
