package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class StopProcessTask extends IndexNameTask {
	public static final TaskName TASK_NAME = TaskName.STOP_PROCESS;
    private static final Chain CHAIN = Chain.STOP_PROCESS_CHAIN;

    private String collectionName;

	public StopProcessTask() {
	    super();
	    this.setSphinxProcessType(SphinxProcessType.SEARCHING);
	}
	
	
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

	@Override
	public String toString() {
	    return "task uid: " + getTaskUID() + ", task status: " + getTaskStatus() + ", task name: " + getTaskName() + ", status: " + getStatus() + ", state: " + getState() + ", coordinator address: " + getCoordinatorAddress();
	}

    @Override
    public Chain getChain() {
        return CHAIN;
    }
}
