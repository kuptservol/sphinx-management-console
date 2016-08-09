package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface ScheduledTaskDao extends Dao<ScheduledTask> {
	List<ScheduledTask> getScheduledTasks();
	List<ScheduledTask> getScheduledTasks(ScheduledTaskType type);
    ScheduledTask getByCollectionName(String collectionName, ScheduledTaskType type);
}
