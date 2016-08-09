package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Port;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ReplicaWrapper implements Serializable {

    @NotEmpty
    private String collectionName;
    private Long replicaNumber;
    @Valid
    private Server server;
    @Port
    private Integer searchPort;
    
    @Port
    private Integer distributedPort;
    
    private Boolean searchServerStatus = false;
    
    private CollectionRoleType collectionType;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Integer getSearchPort() {
        return searchPort;
    }

    public void setSearchPort(Integer searchPort) {
        this.searchPort = searchPort;
    }

    public Long getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Long replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

	public Boolean getSearchServerStatus() {
		return searchServerStatus;
	}

	public void setSearchServerStatus(Boolean searchServerStatus) {
		this.searchServerStatus = searchServerStatus;
	}

	public Integer getDistributedPort() {
		return distributedPort;
	}

	public void setDistributedPort(Integer distributedPort) {
		this.distributedPort = distributedPort;
	}

	public CollectionRoleType getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(CollectionRoleType collectionType) {
		this.collectionType = collectionType;
	}
    
    
}
