package ru.skuptsov.sphinx.console.coordinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.validator.constraints.Range;
import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Port;

import javax.validation.constraints.NotNull;

public class SearchConfigurationPortWrapper {
	@JsonIgnore
	private Object id;
	
    public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

    @NotNull
    @Port
    private Integer searchConfigurationPort;

    public SearchConfigurationPortWrapper() {
    }

    public SearchConfigurationPortWrapper(Integer searchConfigurationPort) {
        this.searchConfigurationPort = searchConfigurationPort;
    }

    public Integer getSearchConfigurationPort() {
        return searchConfigurationPort;
    }

    public void setSearchConfigurationPort(Integer searchConfigurationPort) {
        this.searchConfigurationPort = searchConfigurationPort;
    }
}
