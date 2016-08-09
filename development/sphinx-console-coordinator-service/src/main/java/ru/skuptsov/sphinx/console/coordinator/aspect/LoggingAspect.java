package ru.skuptsov.sphinx.console.coordinator.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.ActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.command.Command;
import ru.skuptsov.sphinx.console.coordinator.task.state.TaskState;
import ru.skuptsov.sphinx.console.spring.service.api.ActivityLogService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

/**
 * Created by Andrey on 13.08.2014.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ActivityLogService activityLogService;

    @Around("execution(public ru.skuptsov.sphinx.console.coordinator.model.Status ru.skuptsov.sphinx.console.coordinator.task.command..execute(..)) && args(task) && @annotation(ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog)")
    public Status log(ProceedingJoinPoint joinPoint, Task task) {
        TaskState currentState = task.getState();
        logger.debug("START EXECUTING TASK STATE: " + currentState);

        ActivityLog activityLog = activityLogService.buildLogStart(task);

        Status status;
        try {
            status = (Status)joinPoint.proceed();   
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            Command command = (Command) joinPoint.getTarget();
            status = Status.build(command.getSystemInterface(), command.getFailureCode(), task.getTaskUID(), e);
            task.setTaskStatus(TaskStatus.FAILURE);
            task.setStatus(status);
        }

        activityLogService.buildLogEnd(task, activityLog);

        if (activityLog.getCollection() != null) {

        	logger.info("TASK PARENT: " + task.getParent() + ", STATE: " + task.getState() + ", SHOULD BE LOGGED: " + task.isShouldBeLogged());
        	
        	if (task.getParent() == null || (task.getParent() != null && currentState != TaskState.COMPLETED)) {
                if (task.isShouldBeLogged()) {
        		    activityLogService.save(activityLog);
                } else {
                	task.setShouldBeLogged(true);
                }
        	}
        }

        logger.debug("END EXECUTING TASK STATE: " + currentState);
        return status;
    }
}
