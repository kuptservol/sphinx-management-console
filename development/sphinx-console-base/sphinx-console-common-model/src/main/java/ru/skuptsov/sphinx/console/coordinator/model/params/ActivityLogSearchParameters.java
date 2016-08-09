package ru.skuptsov.sphinx.console.coordinator.model.params;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;

/**
 * Created by Andrey on 06.08.2014.
 */
public class ActivityLogSearchParameters extends PageParameters {

    private static final long serialVersionUID = -7788619177798333712L;

    private Integer pageSize;
    private Integer pageNumber;
    private Date dateFrom;
    private Date dateTo;
    private String collectionName;
    private List<TaskName> taskNames;
    private TaskStatus taskStatus;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

    public List<TaskName> getTaskNames() {
        return taskNames;
    }

    public void setTaskNames(List<TaskName> taskNames) {
        this.taskNames = taskNames;
    }
}
