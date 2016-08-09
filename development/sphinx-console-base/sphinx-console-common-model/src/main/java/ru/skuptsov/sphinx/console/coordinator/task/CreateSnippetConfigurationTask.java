package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class CreateSnippetConfigurationTask extends SnippetTask {
	public static final TaskName TASK_NAME = TaskName.CREATE_SNIPPET_CONFIGURATION;
    private static final Chain CHAIN = Chain.CREATE_SNIPPET_CONFIGURATION_CHAIN;
    
    private String collectionName;
    
  
    public CreateSnippetConfigurationTask() {
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
	public Chain getChain() {
	    return CHAIN;
	}

	@Override
	public String toString() {
		return "task uid: " + getTaskUID() + ", task status: " + getTaskStatus() + ", task name: " + getTaskName() + ", status: " + getStatus() + ", state: " + getState() + ", coordinator address: " + getCoordinatorAddress();
	}

}
