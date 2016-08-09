package ru.skuptsov.sphinx.console.coordinator.task.command;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildCollectionTask;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;

@Component("STOP_INDEXING")
@Scope("prototype")
public class StopIndexingCommand <T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {

    public static final String AGENT_REMOTE_METHOD_NAME = "stopIndexing";

    @Autowired
    protected TasksMapService tasksMapService;

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        logger.info("ABOUT TO STOP INDEXING, FOR: " + task.getCollectionName());
        RebuildCollectionTask rebuildCollectionTask = null;
        Collection<Task> tasks = tasksMapService.getAllTasks();

        for (Task tasksInProcessing : tasks) {
            if (tasksInProcessing instanceof RebuildCollectionTask &&
                    tasksInProcessing.getTaskStatus() == TaskStatus.RUNNING &&
                    tasksInProcessing.getCollectionName().equals(task.getCollectionName())
                    && tasksInProcessing.getParent() == null) {
                rebuildCollectionTask = (RebuildCollectionTask) tasksInProcessing;
                logger.info("SUB TASKS: " + rebuildCollectionTask.getSubTasks());
                logger.info("ABOUT TO STOP REBUILD COLLECTION TASK: " + rebuildCollectionTask);
                rebuildCollectionTask.setTaskStatus(TaskStatus.STOPPED);
                logger.info("STOPPED REBUILD COLLECTION TASK, FOR: " + rebuildCollectionTask.getCollectionName() + ", " + rebuildCollectionTask.getTaskStatus());
                break;
            }

        }


        Object[] arguments = {task, task.getProcessName()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getIndexAgentAddress();
    }

}
