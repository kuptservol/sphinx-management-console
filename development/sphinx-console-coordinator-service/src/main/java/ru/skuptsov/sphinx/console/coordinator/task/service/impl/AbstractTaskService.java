package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.command.AgentCommand;
import ru.skuptsov.sphinx.console.coordinator.task.command.Command;
import ru.skuptsov.sphinx.console.coordinator.task.command.CommandStrategyFactory;
import ru.skuptsov.sphinx.console.coordinator.task.command.SyncCommand;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.DbCommand;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskService;
import ru.skuptsov.sphinx.console.coordinator.task.state.TaskState;

import java.text.MessageFormat;

public class AbstractTaskService<T extends Task> implements TaskService<T> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean inTransaction = false;

    @Autowired
    protected CommandStrategyFactory commandStrategyFactory; 


    public Command getCommand(String commandName) {
        logger.debug("Searching command " + commandName);
    	Command command = commandStrategyFactory.getCommand(commandName);
        logger.debug(MessageFormat.format("Found command {0} for state {1}", command, commandName));
        return command;
    }

    /*Сейчас транзакционность цепочки команд не работает, т.к. не подключен менеджер транзакций
    * Транзакциоя цепочки команд может быть длительным и небезопасным процессом, т.к. при неправильном использовании
    * (например при наличии в стейте команд агента) транзакции могут быть длительными и вызывать блокировки БД
    * TODO необходимо либо принять решение о допустимости транзакционного контекста в данном варианте,
    * либо реализовывать транзакции только в модуле sphinx.console-dao-hibernate-spring, что безопаснее
    * (как в случае с удалением коллекции CollectionServiceImpl.deleteAllCollectionData)*/
/*    @Transactional(rollbackFor=Exception.class)
    private Status executeInTransaction(T task){
        logger.info("ENTER IN TRANSACTION");
        inTransaction = true;
        return execute(task);
    }
*/
    @Override
    public Status execute(T task) {
        TaskStatus taskStatus = task.getTaskStatus();
        TaskState currentState = task.getState();
    	logger.info("EXECUTING, TASK STATUS: " + taskStatus + ", UID: " + task.getTaskUID());
/*        // enter in transaction if state is the first state of transaction in the chain
        Status transactionStatus = null;
        if(!inTransaction && task.getChain().isFirstInTransaction(currentState)){
            transactionStatus = executeInTransaction(task);
            // mark that transaction is finished;
            inTransaction = false;
            logger.info("FINISH TRANSACTION");
        }
        // exit from transaction
        if(inTransaction && !(currentState instanceof TransactionalTaskState)){
            return transactionStatus;
        }
*/
        if (taskStatus != null && taskStatus == TaskStatus.PAUSED || taskStatus == TaskStatus.STOPPED  || taskStatus == TaskStatus.SUCCESS) {
    		return task.getStatus();
    	}

        Command command = getCommand(currentState.getStateName());
        logger.debug(MessageFormat.format("Command : {0}. For task: {1}. Task state {2}", command, task, currentState));
        if(command != null) {
            if(command instanceof DbCommand || command instanceof SyncCommand) {
                Status status = command.execute(task);
                status = handleStatus(task, status);
                if (status.getCode() == Status.SUCCESS_CODE) {
                    return execute(task);
                } else {
                    return status;
                }
            } else if(command instanceof AgentCommand) {
                return command.execute(task);
            }
        }

        return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

    @Override
    public Status handleAgentCallback(T task, Status status) {
        status = handleStatus(task, status);
        if (status.getCode() == Status.SUCCESS_CODE) {
            return execute(task);
        } else {
            logger.error("Error occurred during agent state execution. Task execution will not proceed. Status: " + status);
            return status;
        }
    }

    private Status handleStatus(T task, Status status) {
        if(task.getTaskStatus() != TaskStatus.SUCCESS){
            if (status.getCode() == Status.SUCCESS_CODE) {
                TaskState currentState = task.getState();
                TaskState nextState = task.getChain().getNextState(currentState);
                logger.info(task + ": change state from " + currentState + " to " + nextState);
                task.setState(nextState);
                if (task.getTaskStatus() != TaskStatus.PAUSED && task.getTaskStatus() != TaskStatus.STOPPED) {
                    task.setTaskStatus(TaskStatus.RUNNING);
                }
                currentState.afterStateExecution(task);
            } else {
                logger.error("Task failure. Status: " + status);
                task.setTaskStatus(TaskStatus.FAILURE);
            }

            task.setStatus(status);
        }
        return status;
    }

}
