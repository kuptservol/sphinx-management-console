package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DistributedCollectionWrapper implements Serializable {
    private static final long serialVersionUID = 190647L;
    
    private List<SimpleCollectionWrapper> nodes = new ArrayList<SimpleCollectionWrapper>();
    
    @NotNull
    @Valid
    private SearchConfigurationPortWrapper searchConfigurationPort;
    @NotNull
    @Valid
	private Configuration searchConfiguration;
    @NotNull
    @Valid
    private Server searchServer;
    
    @NotNull
    @Valid
    private Collection collection;
    

	public List<SimpleCollectionWrapper> getNodes() {
		return nodes;
	}

	public void setNodes(List<SimpleCollectionWrapper> nodes) {
		this.nodes = nodes;
	}

	public SearchConfigurationPortWrapper getSearchConfigurationPort() {
		return searchConfigurationPort;
	}

	public void setSearchConfigurationPort(
			SearchConfigurationPortWrapper searchConfigurationPort) {
		this.searchConfigurationPort = searchConfigurationPort;
	}

	

	public Configuration getSearchConfiguration() {
		return searchConfiguration;
	}

	public void setSearchConfiguration(Configuration searchConfiguration) {
		this.searchConfiguration = searchConfiguration;
	}

	

	public Server getSearchServer() {
		return searchServer;
	}

	public void setSearchServer(Server searchServer) {
		this.searchServer = searchServer;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	
    
    
    
}
