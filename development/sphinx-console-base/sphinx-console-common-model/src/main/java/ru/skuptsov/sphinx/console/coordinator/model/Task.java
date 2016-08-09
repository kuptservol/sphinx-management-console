package ru.skuptsov.sphinx.console.coordinator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.state.TaskState;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String taskUID;
    private String parentTaskUID;
    private TaskStatus taskStatus;
    private String coordinatorAddress;
    private Status status;
    private SphinxProcess sphinxProcess;
    private SphinxProcessType sphinxProcessType;
    private Server indexServer;
    private Server searchServer;
    private String indexAgentAddress;
    private String searchAgentAddress;
    private Integer searchConfigurationPort;
    private Integer newSearchConfigurationPort;
    private Integer distributedConfigurationPort;
    private Integer newDistributedConfigurationPort;
    protected Configuration searchConfiguration;
    protected Configuration indexConfiguration;
    private Date startDate;
    private TaskState state;
    private Long replicaNumber = 1L;
    private String mainSqlQuery;
    private String deltaSqlQuery;
    private String tableName;
    private Boolean isStrictCopy = false;
    private boolean shouldBeLogged = true;
    
    private CollectionType type;
    
    private Task parent;
    private Chain subflowChain;
    List<Task> subTasks;
    private AtomicInteger processedSubTasks = new AtomicInteger();
    
    private IndexType indexType = IndexType.DELTA;
    
    private MergeOption mergeOption;
    
    private boolean pushIndexFilesForReplica = false;

    private boolean snippetFullRebuild = false;
    
    private String snippetTaskUID;
    
    public Task() {
    	this.taskUID = UUID.randomUUID().toString();
    	this.taskStatus = TaskStatus.RUNNING;
        this.status = Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, this.getTaskUID());
        this.state = getChain().getFirstState();
    }
    
	public String getTaskUID() {
		return taskUID;
	}
	public void setTaskUID(String taskUID) {
		this.taskUID = taskUID;
	}

	public String getCoordinatorAddress() {
		return coordinatorAddress;
	}

	public void setCoordinatorAddress(String coordinatorAddress) {
		this.coordinatorAddress = coordinatorAddress;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getIndexAgentAddress() {
		return indexAgentAddress;
	}

	public void setIndexAgentAddress(String indexAgentAddress) {
		this.indexAgentAddress = indexAgentAddress;
	}

	public String getSearchAgentAddress() {
		return searchAgentAddress;
	}

	public void setSearchAgentAddress(String searchAgentAddress) {
		this.searchAgentAddress = searchAgentAddress;
	}

	public TaskStatus getTaskStatus() {
		if (taskStatus == null) {
			if (status.getCode() == Status.SUCCESS_CODE) {
				return TaskStatus.SUCCESS;
			} else {
				return TaskStatus.FAILURE;
			}
		}
		return taskStatus;
	}

    public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public SphinxProcessType getSphinxProcessType() {
		return sphinxProcessType;
	}

	public void setSphinxProcessType(SphinxProcessType sphinxProcessType) {
		this.sphinxProcessType = sphinxProcessType;
	}

    public SphinxProcess getSphinxProcess() {
        return sphinxProcess;
    }

    public void setSphinxProcess(SphinxProcess sphinxProcess) {
        this.sphinxProcess = sphinxProcess;
    }

    public Server getIndexServer() {
		return indexServer;
	}

	public void setIndexServer(Server indexServer) {
		this.indexServer = indexServer;
	}

	public Server getSearchServer() {
		return searchServer;
	}

	public void setSearchServer(Server searchServer) {
		this.searchServer = searchServer;
	}

	public String getIndexServerName() {
        String indexServerName = "";
        if(indexServer != null) {
            indexServerName = indexServer.getName();
        }

        return indexServerName;
    }
	
	public String getSearchServerName() {
        String searchServerName = "";
        if(searchServer != null) {
        	searchServerName = searchServer.getName();
        }

        return searchServerName;
    }

	public abstract String getCollectionName();

	public abstract TaskName getTaskName();

    public Long getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Long replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public String getProcessName() {
        return getCollectionName() + "_" + getReplicaNumber();
    }

    public abstract Chain getChain();
    
    public Task getParent() {
		return parent;
	}

	public void setParent(Task parent) {
		this.parent = parent;
	}
	
	public Chain getSubflowChain() {
		return subflowChain;
	}
	
	public List<Task> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(List<Task> subTasks) {
		this.subTasks = subTasks;
	}

	public void setSubflowChain(Chain subflowChain) {
		this.subflowChain = subflowChain;
	}
	
	public AtomicInteger getProcessedSubTasks() {
		return processedSubTasks;
	}

	public void setProcessedSubTasks(AtomicInteger processedSubTasks) {
		this.processedSubTasks = processedSubTasks;
	}

	public Configuration getSearchConfiguration() {
		return searchConfiguration;
	}

	public void setSearchConfiguration(Configuration searchConfiguration) {
		this.searchConfiguration = searchConfiguration;
	}

	public Configuration getIndexConfiguration() {
		return indexConfiguration;
	}

	public void setIndexConfiguration(Configuration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

    public Integer getSearchConfigurationPort() {
        return searchConfigurationPort;
    }

    public void setSearchConfigurationPort(Integer searchConfigurationPort) {
        this.searchConfigurationPort = searchConfigurationPort;
    }

    public Integer getNewSearchConfigurationPort() {
        return newSearchConfigurationPort;
    }

    public void setNewSearchConfigurationPort(Integer newSearchConfigurationPort) {
        this.newSearchConfigurationPort = newSearchConfigurationPort;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public String getMainSqlQuery() {
		return mainSqlQuery;
	}

	public void setMainSqlQuery(String mainSqlQuery) {
		this.mainSqlQuery = mainSqlQuery;
	}

	public String getDeltaSqlQuery() {
		return deltaSqlQuery;
	}

	public void setDeltaSqlQuery(String deltaSqlQuery) {
		this.deltaSqlQuery = deltaSqlQuery;
	}

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public IndexType getIndexType() {
		return indexType;
	}

	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;
	}

    public boolean isPushIndexFilesForReplica() {
		return pushIndexFilesForReplica;
	}

	public void setPushIndexFilesForReplica(boolean pushIndexFilesForReplica) {
		this.pushIndexFilesForReplica = pushIndexFilesForReplica;
	}

	public MergeOption getMergeOption() {
		return mergeOption;
	}

	public void setMergeOption(MergeOption mergeOption) {
		this.mergeOption = mergeOption;
	}



	public static class MergeOption implements Serializable {
		private String fieldName;
		private String fieldValueFrom;
        private String fieldValueTo;
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

        public String getFieldValueFrom() {
            return fieldValueFrom;
        }

        public void setFieldValueFrom(String fieldValueFrom) {
            this.fieldValueFrom = fieldValueFrom;
        }

        public String getFieldValueTo() {
            return fieldValueTo;
        }

        public void setFieldValueTo(String fieldValueTo) {
            this.fieldValueTo = fieldValueTo;
        }
    }

    public Boolean getStrictCopy() {
        return isStrictCopy;
    }

    public void setStrictCopy(Boolean strictCopy) {
        isStrictCopy = strictCopy;
    }

	public CollectionType getType() {
		return type;
	}

	public void setType(CollectionType type) {
		this.type = type;
	}

	public String getParentTaskUID() {
		return parentTaskUID;
	}

	public void setParentTaskUID(String parentTaskUID) {
		this.parentTaskUID = parentTaskUID;
	}

	public boolean isShouldBeLogged() {
		return shouldBeLogged;
	}

	public void setShouldBeLogged(boolean shouldBeLogged) {
		this.shouldBeLogged = shouldBeLogged;
	}

	public Integer getDistributedConfigurationPort() {
		return distributedConfigurationPort;
	}

	public void setDistributedConfigurationPort(Integer distributedConfigurationPort) {
		this.distributedConfigurationPort = distributedConfigurationPort;
	}

	public Integer getNewDistributedConfigurationPort() {
		return newDistributedConfigurationPort;
	}

	public void setNewDistributedConfigurationPort(
			Integer newDistributedConfigurationPort) {
		this.newDistributedConfigurationPort = newDistributedConfigurationPort;
	}

	public boolean isSnippetFullRebuild() {
		return snippetFullRebuild;
	}

	public void setSnippetFullRebuild(boolean snippetFullRebuild) {
		this.snippetFullRebuild = snippetFullRebuild;
	}

	public String getSnippetTaskUID() {
		return snippetTaskUID;
	}

	public void setSnippetTaskUID(String snippetTaskUID) {
		this.snippetTaskUID = snippetTaskUID;
	}
    
     
}
