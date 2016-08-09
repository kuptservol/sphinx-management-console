package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class StopRebuildSnippetsTask extends IndexNameTask {
	public static final TaskName TASK_NAME = TaskName.STOP_REBUILD_SNIPPETS;
    private static final Chain CHAIN = Chain.STOP_REBUILD_SNIPPETS_CHAIN;

	private String collectionName;

	public StopRebuildSnippetsTask() {
    	super();
    	this.setSphinxProcessType(SphinxProcessType.INDEXING);
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
    public Chain getChain() {
        return CHAIN;
    }

}
