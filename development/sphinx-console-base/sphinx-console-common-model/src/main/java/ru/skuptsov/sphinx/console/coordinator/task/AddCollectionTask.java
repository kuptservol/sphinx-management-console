package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class AddCollectionTask extends ProcessTask {
	public static final TaskName TASK_NAME = TaskName.ADD_COLLECTION;
    private static final Chain CHAIN = Chain.ADD_COLLECTION_CHAIN;

    private Integer searchConfigurationPort;
    private Integer distributedConfigurationPort;
    private Configuration indexConfiguration;
    private Configuration searchConfiguration;
    
    

    public AddCollectionTask() {
    	super();
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

    public Integer getSearchConfigurationPort() {
        return searchConfigurationPort;
    }

    public void setSearchConfigurationPort(Integer searchConfigurationPort) {
        this.searchConfigurationPort = searchConfigurationPort;
    }
    
    
    
	public Integer getDistributedConfigurationPort() {
		return distributedConfigurationPort;
	}

	public void setDistributedConfigurationPort(Integer distributedConfigurationPort) {
		this.distributedConfigurationPort = distributedConfigurationPort;
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
