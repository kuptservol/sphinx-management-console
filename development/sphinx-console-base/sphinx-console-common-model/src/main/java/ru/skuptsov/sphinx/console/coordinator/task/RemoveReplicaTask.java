package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class RemoveReplicaTask extends IndexNameTask {
	public static final TaskName TASK_NAME = TaskName.REMOVE_REPLICA;
    private static final Chain CHAIN = Chain.REMOVE_REPLICA_CHAIN;

	private String collectionName;

    public RemoveReplicaTask() {
    	super();
    	this.setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

	@Override
	public TaskName getTaskName() {
		return TASK_NAME;
	}

    @Override
    public Chain getChain() {
        return CHAIN;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
