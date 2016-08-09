package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;

import java.util.List;

public interface ScheduledTaskService extends Service<ScheduledTask> {
	List<ScheduledTask> getScheduledTasks();
    List<ScheduledTask> getScheduledTasks(ScheduledTaskType type);
    ScheduledTask findByCollectionName(String collectionName, ScheduledTaskType type);
}
