package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class ModifyReplicaTask extends ProcessTask {
	public static final TaskName TASK_NAME = TaskName.MODIFY_REPLICA_PORT;
    private static final Chain CHAIN = Chain.MODIFY_REPLICA_PORT_CHAIN;

	private String collectionName;

    public ModifyReplicaTask() {
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
