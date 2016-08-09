package ru.skuptsov.sphinx.console.coordinator.model.params;

public class TaskLogsSearchParameters extends PageParameters {
    private String taskUid;
    private Long replicaNumber;
    private Long processId;
    private Boolean last;
    private String operationType;

	public String getTaskUid() {
		return taskUid;
	}

	public void setTaskUid(String taskUid) {
		this.taskUid = taskUid;
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

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
