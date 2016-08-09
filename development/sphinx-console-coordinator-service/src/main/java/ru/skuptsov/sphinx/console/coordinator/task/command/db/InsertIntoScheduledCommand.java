package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.MergeCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
@Component("INSERT_INTO_SCHEDULED_TASK")
@Scope("prototype")
public class InsertIntoScheduledCommand<T extends ProcessTask> extends DbCommand<ProcessTask> {
    @Autowired
	private CollectionService collectionService;
    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private RebuildCollectionTaskScheduler rebuildCollectionTaskScheduler;

    @Autowired
    private MergeCollectionTaskScheduler mergeCollectionTaskScheduler;

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("INSERT_INTO_SCHEDULED_TASK EXECUTION...");

        Collection collection = collectionService.getCollection(task.getCollectionName());
        ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.INDEXING_DELTA);
        if(scheduledTask == null){
            scheduledTask = new ScheduledTask();
            scheduledTask.setCollection(collection);
            scheduledTask.setIsEnabled(true);
            scheduledTask.setType(ScheduledTaskType.INDEXING_DELTA);
        }
        scheduledTask.setCronSchedule(task.getCronSchedule());
        rebuildCollectionTaskScheduler.addScheduler(scheduledTask);

        ScheduledTask scheduledTaskMergeDelta = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.MERGE_DELTA);
        if(task.getMergeDeltaCronSchedule() != null) {
            if(scheduledTaskMergeDelta == null){
                scheduledTaskMergeDelta = new ScheduledTask();
                scheduledTaskMergeDelta.setCollection(collectionService.getCollection(task.getCollectionName()));
                scheduledTaskMergeDelta.setIsEnabled(true);
                scheduledTaskMergeDelta.setType(ScheduledTaskType.MERGE_DELTA);
            }
            scheduledTaskMergeDelta.setCronSchedule(task.getMergeDeltaCronSchedule());
            mergeCollectionTaskScheduler.addScheduler(scheduledTaskMergeDelta);        }

        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        return status;
    }
}
