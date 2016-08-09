package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 14.08.14
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */
public abstract class ModifyCollectionAttributesTask extends ReplicaLoopTask {
    private Integer searchConfigurationPort;
    private Integer distributedConfigurationPort;
    private Configuration searchConfiguration;
    private Configuration indexConfiguration;

    public ModifyCollectionAttributesTask() {
        super();
        setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

    public Configuration getSearchConfiguration() {
        return searchConfiguration;
    }

    public void setSearchConfiguration(Configuration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    public Configuration getIndexConfiguration() {
        return indexConfiguration;
    }

    public void setIndexConfiguration(Configuration indexConfiguration) {
        this.indexConfiguration = indexConfiguration;
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
    
    
    
}
