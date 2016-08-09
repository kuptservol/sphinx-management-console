package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.ActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;

import java.util.Date;
import java.util.List;

public interface ActivityLogService extends Service<ActivityLog> {
    List<ActivityLog> getActivityLog(ActivityLogSearchParameters parameters);
    ActivityLog buildLogStart(Task task);
    void buildLogEnd(Task task, ActivityLog activityLog);
    Long countActivityLog(ActivityLogSearchParameters parameters);
    List<ActivityLog> getTaskLog(TaskLogsSearchParameters parameters);
    Long countTaskLog(TaskLogsSearchParameters parameters);
    ActivityLog getLast(String taskUid);
    List<ActivityLog> getSphinxProcessLogs(SphinxProcess process);
    ActivityLog getLast(String collectionName, TaskName taskName, Date afterDate);
    String getLastSnippetLogTaskUid(String collectionName);
}
