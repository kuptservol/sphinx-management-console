package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.ActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.Date;
import java.util.List;


public interface ActivityLogDao extends Dao<ActivityLog> {
	List<ActivityLog> find(ActivityLogSearchParameters parameters);
	Long countActivityLog(ActivityLogSearchParameters parameters);
	List<ActivityLog> getTaskLog(TaskLogsSearchParameters parameters);
    Long countTaskLog(TaskLogsSearchParameters parameters);
    ActivityLog getLast(String taskUid);
    List<ActivityLog> getSphinxProcessLogs(SphinxProcess sphinxProcess);
    ActivityLog getLast(String collectionName, TaskName taskName, Date afterDate);
    String getLastSnippetLogTaskUid(String collectionName);
}
