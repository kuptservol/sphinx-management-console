package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 14.08.14
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class DeleteProcessFromServerTask extends IndexNameTask {

    public static final TaskName TASK_NAME = TaskName.DELETE_PROCESS_FROM_SERVER;
    private static final Chain CHAIN = Chain.DELETE_PROCESS_FROM_SERVER_CHAIN;

    private String collectionName;
    private Long serverId;

    public DeleteProcessFromServerTask() {
        super();
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    @Override
    public Chain getChain() {
        return CHAIN;
    }
}
