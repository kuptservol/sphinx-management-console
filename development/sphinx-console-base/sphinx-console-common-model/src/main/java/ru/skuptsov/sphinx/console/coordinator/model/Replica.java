package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Replica extends BaseEntity {
	private Long id;
    @NotNull
    private Long number;
    @NotNull
//    @Valid
    private Collection collection;
    @Valid
    private SphinxProcess searchProcess;
    private String searchAgentAddress;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getSearchAgentAddress() {
        return searchAgentAddress;
    }

    public void setSearchAgentAddress(String searchAgentAddress) {
        this.searchAgentAddress = searchAgentAddress;
    }

    public SphinxProcess getSearchProcess() {
        return searchProcess;
    }

    public void setSearchProcess(SphinxProcess searchProcess) {
        this.searchProcess = searchProcess;
    }
}
