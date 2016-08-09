package ru.skuptsov.sphinx.console.coordinator.model;


import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Port;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DistributedConfigurationPortWrapper {
	@JsonIgnore
	private Object id;
	
    public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

    @Port
    private Integer distributedConfigurationPort;

    public DistributedConfigurationPortWrapper() {
    }

    public DistributedConfigurationPortWrapper(Integer distributedConfigurationPort) {
        this.distributedConfigurationPort = distributedConfigurationPort;
    }

    public Integer getDistributedConfigurationPort() {
        return distributedConfigurationPort;
    }

    public void setDistributedConfigurationPort(Integer distributedConfigurationPort) {
        this.distributedConfigurationPort = distributedConfigurationPort;
    }
}
