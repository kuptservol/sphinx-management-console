package ru.skuptsov.sphinx.console.coordinator.model;

import java.util.HashSet;
import java.util.Set;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

public class DistributedCollectionNode extends BaseEntity {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	private Collection distributedCollection;
	
	private Collection simpleCollection;
	
	private Set<DistributedCollectionAgent> distributedCollectionAgents = new HashSet<DistributedCollectionAgent>();

	public Collection getDistributedCollection() {
		return distributedCollection;
	}

	public void setDistributedCollection(Collection distributedCollection) {
		this.distributedCollection = distributedCollection;
	}

	public Collection getSimpleCollection() {
		return simpleCollection;
	}

	public void setSimpleCollection(Collection simpleCollection) {
		this.simpleCollection = simpleCollection;
	}

	public Set<DistributedCollectionAgent> getDistributedCollectionAgents() {
		return distributedCollectionAgents;
	}

	public void setDistributedCollectionAgents(
			Set<DistributedCollectionAgent> distributedCollectionAgents) {
		this.distributedCollectionAgents = distributedCollectionAgents;
	}
	
	
}
