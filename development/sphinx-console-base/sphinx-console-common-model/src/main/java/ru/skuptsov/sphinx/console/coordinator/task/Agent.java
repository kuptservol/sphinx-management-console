package ru.skuptsov.sphinx.console.coordinator.task;

import java.io.Serializable;

public class Agent implements Serializable {
    private Long sphinxProcessId;
    private String nodeHost;
    private String nodeDistribPort;
    
	public Long getSphinxProcessId() {
		return sphinxProcessId;
	}
	public void setSphinxProcessId(Long sphinxProcessId) {
		this.sphinxProcessId = sphinxProcessId;
	}
	public String getNodeHost() {
		return nodeHost;
	}
	public void setNodeHost(String nodeHost) {
		this.nodeHost = nodeHost;
	}
	public String getNodeDistribPort() {
		return nodeDistribPort;
	}
	public void setNodeDistribPort(String nodeDistribPort) {
		this.nodeDistribPort = nodeDistribPort;
	}
    
    
}
