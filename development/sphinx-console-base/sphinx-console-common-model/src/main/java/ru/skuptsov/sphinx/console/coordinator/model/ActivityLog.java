package ru.skuptsov.sphinx.console.coordinator.model;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import java.util.Date;

public class ActivityLog extends BaseEntity {
	private Long id;
    private String taskName;
    private String taskUid;
    private Date date;
    private Collection collection;
    private String indexName;
    private Server server;
    private String serverName;
    private SphinxProcess process;
    private SphinxProcessType sphinxProcessType;
    private Date startTime;
    private Date endTime;
    private String operationType;
    private StageStatus status;
    private TaskStatus taskStatus;
    private String exceptionText;
    private byte[] data;
    private Date taskStartTime;
    private Date taskEndTime;
    private Long replicaNumber;

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

    
    public String getTaskUid() {
		return taskUid;
	}

	public void setTaskUid(String taskUid) {
		this.taskUid = taskUid;
	}

	public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public SphinxProcess getProcess() {
        return process;
    }

    public void setProcess(SphinxProcess process) {
        this.process = process;
    }

    public SphinxProcessType getSphinxProcessType() {
        return sphinxProcessType;
    }

    public void setSphinxProcessType(SphinxProcessType sphinxProcessType) {
        this.sphinxProcessType = sphinxProcessType;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getExceptionText() {
        return exceptionText;
    }

    public void setExceptionText(String exceptionText) {
        this.exceptionText = exceptionText;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

	public StageStatus getStatus() {
		return status;
	}

	public void setStatus(StageStatus status) {
		this.status = status;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getTaskStartTime() {
		return taskStartTime;
	}

	public void setTaskStartTime(Date taskStartTime) {
		this.taskStartTime = taskStartTime;
	}

	public Date getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

    public Long getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Long replicaNumber) {
        this.replicaNumber = replicaNumber;
    }
}
