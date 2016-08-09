package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleCollectionWrapper implements Serializable {
	private static final long serialVersionUID = 270647L;
	 
	private String collectionName;
	 
	private List<SimpleCollectionReplicaWrapper> agents = new ArrayList<SimpleCollectionReplicaWrapper>();

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public List<SimpleCollectionReplicaWrapper> getAgents() {
		return agents;
	}

	public void setAgents(List<SimpleCollectionReplicaWrapper> agents) {
		this.agents = agents;
	}
	 
	 
}
