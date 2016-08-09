package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class AddDistributedCollectionTask extends DistributedTask {
	public static final TaskName TASK_NAME = TaskName.ADD_DISTRIBUTED_COLLECTION;
	
	private static final Chain CHAIN = Chain.ADD_DISTRIBUTED_COLLECTION_CHAIN;
	
	private Integer searchConfigurationPort;
	private Configuration indexConfiguration;
	private Configuration searchConfiguration;
	
	
	public AddDistributedCollectionTask() {
    	super();
    }
	
	
	
	public Integer getSearchConfigurationPort() {
		return searchConfigurationPort;
	}



	public void setSearchConfigurationPort(Integer searchConfigurationPort) {
		this.searchConfigurationPort = searchConfigurationPort;
	}



	public Configuration getIndexConfiguration() {
		return indexConfiguration;
	}



	public void setIndexConfiguration(Configuration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
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
