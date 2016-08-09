package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildSnippetTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;

@Component("DISABLE_SNIPPET_SCHEDULED")
@Scope("prototype")
public class DisableSnippetScheduledCommand<T extends ProcessTask> extends DbCommand<T> {
	@Autowired
    private ScheduledTaskService scheduledTaskService;
    
    @Autowired
	private RebuildSnippetTaskScheduler rebuildSnippetTaskScheduler;
    
    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("DISABLE_SNIPPET_SCHEDULED_TASK EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.BUILD_SNIPPET);
        if(scheduledTask != null) {
            task.setSnippetSchedulingEnabledBeforeTaskExecution(scheduledTask.getIsEnabled());

            rebuildSnippetTaskScheduler.disableScheduling(scheduledTask);
        }
        
       

        return status;
    }
}
