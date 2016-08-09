package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.MergeCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 13.08.14
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
@Component("DELETE_FROM_SCHEDULED_TASK")
@Scope("prototype")
public class DeleteFromScheduledCommand<T extends Task> extends DbCommand<T> {
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    
    @Autowired
	private RebuildCollectionTaskScheduler rebuildCollectionTaskScheduler;

    @Autowired
    private MergeCollectionTaskScheduler mergeCollectionTaskScheduler;

    @Override
    @SaveActivityLog
    public Status execute(Task task) {
        logger.debug("DELETE_FROM_SCHEDULED_TASK EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.INDEXING_DELTA);
        if(scheduledTask != null) {
            rebuildCollectionTaskScheduler.deleteScheduler(scheduledTask);
        }

        ScheduledTask scheduledTaskMergeDelta = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.MERGE_DELTA);
        if(scheduledTaskMergeDelta != null) {
            mergeCollectionTaskScheduler.deleteScheduler(scheduledTaskMergeDelta);
        }

        return status;
    }
}