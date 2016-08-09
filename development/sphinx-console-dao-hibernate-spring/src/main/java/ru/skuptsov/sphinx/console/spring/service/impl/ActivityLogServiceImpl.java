package ru.skuptsov.sphinx.console.spring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.state.TaskState;
import ru.skuptsov.sphinx.console.dao.api.ActivityLogDao;
import ru.skuptsov.sphinx.console.spring.service.api.ActivityLogService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

import java.util.Date;
import java.util.List;


@Service
public class ActivityLogServiceImpl extends AbstractSpringService<ActivityLogDao, ActivityLog> implements ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private ServerService serverService;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivityLog(ActivityLogSearchParameters parameters) {
        return getDao().find(parameters);
    }

    @Override
    @Transactional(readOnly = true)
    public String getLastSnippetLogTaskUid(String collectionName) {
        return getDao().getLastSnippetLogTaskUid(collectionName);
    }

    @Override
    public ActivityLog buildLogStart(Task task){

        TaskState currentState = task.getState();

        ActivityLog activityLog = new ActivityLog();
        activityLog.setTaskUid(task.getParentTaskUID() != null ? task.getParentTaskUID() : task.getTaskUID());
        activityLog.setDate(new Date());
        activityLog.setOperationType(currentState.toString());
        activityLog.setSphinxProcessType(task.getSphinxProcessType());
        activityLog.setIndexName(task.getCollectionName());
        activityLog.setReplicaNumber(task.getReplicaNumber());
        activityLog.setTaskName(task.getTaskName().getTitle());
        activityLog.setStartTime(new Date());
        activityLog.setTaskStartTime(task.getStartDate());
        activityLog.setProcess(task.getSphinxProcess());

        String serverName = null;

        if (task.getSphinxProcessType() == SphinxProcessType.SEARCHING) {
            serverName = task.getSearchServerName();
        } else if (task.getSphinxProcessType() == SphinxProcessType.INDEXING || task.getSphinxProcessType() == SphinxProcessType.FULL_INDEXING) {
            serverName = task.getIndexServerName();
        }

        logger.info("PERFORMED TASK ON: " + serverName);

        Server server = serverService.getServer(task.getSphinxProcessType(), serverName);

        if (server == null) {
            server = serverService.getCoordinator();
        }

        logger.info("PERFORMED TASK ON: " + server);

        if (server != null) {
            activityLog.setServer(server);
            activityLog.setServerName(server.getName());
        }

        return activityLog;

    }

    @Override
    public void buildLogEnd(Task task, ActivityLog activityLog) {

        activityLog.setCollection(collectionService.getCollection(task.getCollectionName()));
        activityLog.setEndTime(new Date());
        activityLog.setTaskStatus(task.getTaskStatus());

        if (task.getTaskStatus() != TaskStatus.RUNNING) {
            activityLog.setTaskEndTime(new Date());
        }
        activityLog.setStatus(task.getStatus().getCode() == Status.SUCCESS_CODE ? StageStatus.SUCCESS : StageStatus.FAILURE);
        activityLog.setExceptionText(task.getStatus().getStackTrace());

    }

    @Override
	@Transactional(readOnly = true)
	public Long countActivityLog(ActivityLogSearchParameters parameters) {
		return getDao().countActivityLog(parameters);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityLog> getTaskLog(TaskLogsSearchParameters parameters) {
		return getDao().getTaskLog(parameters);
	}

	@Override
	@Transactional(readOnly = true)
	public Long countTaskLog(TaskLogsSearchParameters parameters) {
		return getDao().countTaskLog(parameters);
	}

	@Override
	@Transactional(readOnly = true)
	public ActivityLog getLast(String taskUid) {
		return getDao().getLast(taskUid);
	}

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getSphinxProcessLogs(SphinxProcess process) {
        return getDao().getSphinxProcessLogs(process);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityLog getLast(String collectionName, TaskName taskName, Date afterDate) {
        return getDao().getLast(collectionName, taskName, afterDate);
    }
}
