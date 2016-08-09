package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class MergeCollectionTask extends ReplicaLoopTask {
	public static final TaskName TASK_NAME = TaskName.MERGE_COLLECTION;
    private static final Chain CHAIN = Chain.MERGE_COLLECTION_CHAIN;

    private String collectionName;

    public MergeCollectionTask() {
        super();
        this.setSphinxProcessType(SphinxProcessType.SEARCHING);
        this.setPushIndexFilesForReplica(true);
    }

    @Override
    public String getCollectionName() {
        return collectionName;	
    }

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    @Override
    public Chain getChain() {
        return CHAIN;
    }

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
    
    
}
