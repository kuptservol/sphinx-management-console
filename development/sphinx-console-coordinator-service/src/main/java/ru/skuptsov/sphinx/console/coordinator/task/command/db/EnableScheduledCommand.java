package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.MergeCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;

@Component("ENABLE_SCHEDULED")
@Scope("prototype")
public class EnableScheduledCommand<T extends ProcessTask> extends DbCommand<T> {
	@Autowired
    private ScheduledTaskService scheduledTaskService;
    
    @Autowired
	private RebuildCollectionTaskScheduler rebuildCollectionTaskScheduler;
    
    @Autowired
	private MergeCollectionTaskScheduler mergeCollectionTaskScheduler;
	
	

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("ENABLE_SCHEDULED_TASK EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.INDEXING_DELTA);
        if(scheduledTask != null && task.isDeltaSchedulingEnabledBeforeTaskExecution()) {
            rebuildCollectionTaskScheduler.enableScheduling(scheduledTask);
        }
        
        ScheduledTask scheduledTaskMergeDelta = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.MERGE_DELTA);
        
        if(scheduledTaskMergeDelta != null && task.isMergeSchedulingEnabledBeforeTaskExecution()) {
            mergeCollectionTaskScheduler.enableScheduling(scheduledTaskMergeDelta);
        }

        return status;
    }
}
