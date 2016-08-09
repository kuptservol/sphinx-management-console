package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class RebuildSnippetsTask extends ReplicaLoopTask {
	public static final TaskName TASK_NAME = TaskName.REBUILD_SNIPPETS;
    private static final Chain CHAIN = Chain.REBUILD_SNIPPETS_CHAIN;
    
	private String collectionName;
	
	private SnippetConfiguration snippetConfiguration;
	 
	public RebuildSnippetsTask() {
	    super();
	    this.setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

	public String getCollectionName() {
		return collectionName;
	}

    public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
    
	public SnippetConfiguration getSnippetConfiguration() {
		return snippetConfiguration;
	}

	public void setSnippetConfiguration(SnippetConfiguration snippetConfiguration) {
		this.snippetConfiguration = snippetConfiguration;
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
