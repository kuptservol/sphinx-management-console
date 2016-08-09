package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class TaskWrapper implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 213461L;
	@NotEmpty
	private String taskUID;
    private String taskName;
    
	public String getTaskUID() {
		return taskUID;
	}
	public void setTaskUID(String taskUID) {
		this.taskUID = taskUID;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
    
    
}
