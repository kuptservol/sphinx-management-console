package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class StartAllProcessesTask extends ProcessTask implements ParallelSubFlowTask  {
	public static final TaskName TASK_NAME = TaskName.START_ALL_PROCESSES;
    private static final Chain CHAIN = Chain.START_ALL_PROCESSES_CHAIN;

    private String collectionName;

	public StartAllProcessesTask() {
	    super();
	    this.setSphinxProcessType(SphinxProcessType.SEARCHING);
	    this.setSubflowChain(Chain.START_ALL_PROCESSES_PARALLEL_CHAIN);
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
	public String getSearchConfigurationName() {
		return null;
	}


	@Override
	public String getIndexConfigurationName() {
		return null;
	}


	@Override
	public Configuration getSearchConfiguration() {
		return null;
	}


	@Override
	public Configuration getIndexConfiguration() {
		return null;
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



