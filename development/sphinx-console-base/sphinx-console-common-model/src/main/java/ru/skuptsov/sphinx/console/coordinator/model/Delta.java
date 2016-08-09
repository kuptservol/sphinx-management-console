package ru.skuptsov.sphinx.console.coordinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class Delta extends BaseEntity {
	private Long id;
	@JsonIgnore
    @Valid
	private Collection collection;
    @NotNull
    private DeltaType type;
    private Date period = new Date();
    @Valid
    private ExternalAction externalAction;
    private String field;
    private Date mergeTime = new Date();
    @Valid
    private DeleteScheme deleteScheme;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public DeltaType getType() {
        return type;
    }

    public void setType(DeltaType type) {
        this.type = type;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public ExternalAction getExternalAction() {
        return externalAction;
    }

    public void setExternalAction(ExternalAction externalAction) {
        this.externalAction = externalAction;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Date getMergeTime() {
        return mergeTime;
    }

    public void setMergeTime(Date mergeTime) {
        this.mergeTime = mergeTime;
    }

    public DeleteScheme getDeleteScheme() {
        return deleteScheme;
    }

    public void setDeleteScheme(DeleteScheme deleteScheme) {
        this.deleteScheme = deleteScheme;
    }
}
