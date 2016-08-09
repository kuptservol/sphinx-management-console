package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class UpdateScheduleWrapper {
	@NotEmpty
    private String collectionName;
	@NotEmpty
    private String cronExpression;
	@NotNull
    private ScheduledTaskType type;
    
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
    public ScheduledTaskType getType() {
        return type;
    }
    public void setType(ScheduledTaskType type) {
        this.type = type;
    }
}
