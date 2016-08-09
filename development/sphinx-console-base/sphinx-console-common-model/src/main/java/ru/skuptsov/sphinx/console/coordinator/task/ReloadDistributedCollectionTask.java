package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class ReloadDistributedCollectionTask extends DistributedReplicaLoopTask {
	public static final TaskName TASK_NAME = TaskName.RELOAD_DISTRIBUTED_COLLECTION;

	private static final Chain CHAIN = Chain.RELOAD_DISTRIBUTED_COLLECTION_CHAIN;

	public ReloadDistributedCollectionTask() {
    	super();
    }

	public Configuration getSearchConfiguration() {
		return searchConfiguration;
	}

	public void setSearchConfiguration(Configuration searchConfiguration) {
		this.searchConfiguration = searchConfiguration;
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
