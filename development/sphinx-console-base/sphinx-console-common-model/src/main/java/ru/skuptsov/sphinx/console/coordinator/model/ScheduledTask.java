package ru.skuptsov.sphinx.console.coordinator.model;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

public class ScheduledTask extends BaseEntity {

	private Long id;
    private Collection collection;
    private String cronSchedule;
    private Boolean isEnabled = true;
    private ScheduledTaskType type;

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

    public String getCronSchedule() {
        return cronSchedule;
    }

    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    public ScheduledTaskType getType() {
        return type;
    }

    public void setType(ScheduledTaskType type) {
        this.type = type;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
