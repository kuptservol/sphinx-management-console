package ru.skuptsov.sphinx.console.coordinator.task;


import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class RebuildCollectionTask extends IndexNameTask implements ParallelSubFlowTask {
	public static final TaskName TASK_NAME=TaskName.REBUILD_COLLECTION;
    private static final Chain CHAIN = Chain.REBUILD_COLLECTION_CHAIN;
    
    
	private String collectionName;
	 
	public RebuildCollectionTask() {
	    super();
	    this.setSphinxProcessType(SphinxProcessType.INDEXING);
        this.setSubflowChain(Chain.REBUILD_COLLECTION_PARALLEL_CHAIN);
        this.setPushIndexFilesForReplica(true);
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
