package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class SimpleCollectionReplicaWrapper implements Serializable, Comparable<SimpleCollectionReplicaWrapper> {
	private static final long serialVersionUID = 330647L;
	 
	private String nodeHost;
	 
	private Integer nodeDistribPort;

	public String getNodeHost() {
		return nodeHost;
	}

	public void setNodeHost(String nodeHost) {
		this.nodeHost = nodeHost;
	}

	public Integer getNodeDistribPort() {
		return nodeDistribPort;
	}

	public void setNodeDistribPort(Integer nodeDistribPort) {
		this.nodeDistribPort = nodeDistribPort;
	}


	@Override
	public int compareTo(SimpleCollectionReplicaWrapper o) {
		return nodeHost.compareTo(o.nodeHost) != 0 ? nodeHost.compareTo(o.nodeHost) : nodeDistribPort.compareTo(o.nodeDistribPort);
	}
}
