package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;
import java.util.Date;

public class TaskDataViewWrapper implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 56474L;
	private Long id;
	private String taskUid;
    private String taskName;
    private String collectionName;
    private Long replicaNumber;
    private Date startTime;
    private Date endTime;
    private TaskStatus status;
    private String stage;
    private StageStatus stageStatus;
    private String serverName;
    private Long processId;
    private String operationType;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getTaskUid() {
		return taskUid;
	}
	public void setTaskUid(String taskUid) {
		this.taskUid = taskUid;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

    public Long getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Long replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public StageStatus getStageStatus() {
        return stageStatus;
    }

    public void setStageStatus(StageStatus stageStatus) {
        this.stageStatus = stageStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskDataViewWrapper that = (TaskDataViewWrapper) o;

        if (taskUid != null ? !taskUid.equals(that.taskUid) : that.taskUid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return taskUid != null ? taskUid.hashCode() : 0;
    }
}
