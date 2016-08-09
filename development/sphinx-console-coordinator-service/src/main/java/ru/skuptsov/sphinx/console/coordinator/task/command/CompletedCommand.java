package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.CoordinatorCommand;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
@Component("COMPLETED")
@Scope("prototype")
public class CompletedCommand<T extends Task> extends CoordinatorCommand<T> implements AsyncCommand {

    @Override
    @SaveActivityLog
    public Status execute(Task task) {
        logger.debug("COMPLETED STATE EXECUTION...");

        task.getState().afterStateExecution(task);

        task.setStatus(Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID()));
        
        if (task.getParent() != null) {
            Task parent = task.getParent();
            logger.info("PARENT TASK PROCESSED SUBTASKS: " + parent.getProcessedSubTasks().get());
            parent.getProcessedSubTasks().incrementAndGet();
            logger.info("PARENT TASK PROCESSED SUBTASKS: " + parent.getProcessedSubTasks().get());
        }

        return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

}
