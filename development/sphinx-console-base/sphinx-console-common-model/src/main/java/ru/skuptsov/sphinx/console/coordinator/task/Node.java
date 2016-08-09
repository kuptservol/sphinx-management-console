package ru.skuptsov.sphinx.console.coordinator.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;

public class Node implements Serializable {
    private Collection simpleCollection;
    private String simpleCollectionName;
    
    private List<Agent> agents = new ArrayList<Agent>();
    
	public Collection getSimpleCollection() {
		return simpleCollection;
	}
	public void setSimpleCollection(Collection simpleCollection) {
		this.simpleCollection = simpleCollection;
	}
	public String getSimpleCollectionName() {
		return simpleCollectionName;
	}
	public void setSimpleCollectionName(String simpleCollectionName) {
		this.simpleCollectionName = simpleCollectionName;
	}
	public List<Agent> getAgents() {
		return agents;
	}
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
    
    
	public void addAgent(Agent agent) {
		if (agent != null) {
			agents.add(agent);
		}
	}
}
