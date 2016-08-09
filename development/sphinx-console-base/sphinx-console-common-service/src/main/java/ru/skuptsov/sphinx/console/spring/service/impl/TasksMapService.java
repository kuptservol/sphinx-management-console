package ru.skuptsov.sphinx.console.spring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 25.08.15
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TasksMapService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<String, Task> tasksMap = new ConcurrentHashMap<String, Task>();

    private boolean getSameTaskRunning(String collectionName, Task checkTask){
        if(checkTask.getParent() == null){
            for(Task task : tasksMap.values()){
                if(task.getParent() == null && task.getTaskStatus() == TaskStatus.RUNNING && task.getCollectionName().equals(collectionName) && task.getTaskName() == checkTask.getTaskName()){
                    logger.info("Found same task: " + task);
                    logger.info("Target task: " + checkTask);
                    return true;
                }
            }
        }
        return false;
    }

    public Status putTask(Task task){
        if(getSameTaskRunning(task.getCollectionName(), task)){
            logger.warn(MessageFormat.format("Can't add task {0}. Same task already running.", task));
            return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.FAILURE_REPEATED_TASK, "Для коллекции " + task.getCollectionName() + " уже выполняется задача " + task.getTaskName() + ". Дождитесь окончания выполнения ранее запущенной задачи или прервите ее выполнение.", task.getTaskUID());
        }
        else {
            tasksMap.put(task.getTaskUID(), task);
            return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE);
        }
    }

    public Task getTask(String taskUid){
        return tasksMap.get(taskUid);
    }

    public Collection<Task> getAllTasks()
    {
        return tasksMap.values();
    }
}
