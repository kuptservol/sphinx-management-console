package ru.skuptsov.sphinx.console.coordinator.model;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

public class DistributedCollectionAgent extends BaseEntity {
	private Long id;
	
	private DistributedCollectionNode distributedCollectionNode;
	
	private String nodeHost;
	
	private Integer nodeDistribPort;
	
	private SphinxProcess sphinxProcess;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DistributedCollectionNode getDistributedCollectionNode() {
		return distributedCollectionNode;
	}

	public void setDistributedCollectionNode(
			DistributedCollectionNode distributedCollectionNode) {
		this.distributedCollectionNode = distributedCollectionNode;
	}

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

	public SphinxProcess getSphinxProcess() {
		return sphinxProcess;
	}

	public void setSphinxProcess(SphinxProcess sphinxProcess) {
		this.sphinxProcess = sphinxProcess;
	}
	
	
}
