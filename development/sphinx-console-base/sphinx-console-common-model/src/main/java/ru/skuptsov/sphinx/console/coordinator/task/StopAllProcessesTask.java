package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class StopAllProcessesTask extends IndexNameTask implements ParallelSubFlowTask {
	public static final TaskName TASK_NAME = TaskName.STOP_ALL_PROCESSES;
    private static final Chain CHAIN = Chain.STOP_ALL_PROCESSES_CHAIN;

    private String collectionName;

	public StopAllProcessesTask() {
	    super();
	    this.setSphinxProcessType(SphinxProcessType.SEARCHING);
	    this.setSubflowChain(Chain.STOP_ALL_PROCESSES_PARALLEL_CHAIN);
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
    	if (getParent() == null) {
    	    return CHAIN;
    	} else {
    		return getSubflowChain();
    	}
    }
}


