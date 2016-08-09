package ru.skuptsov.sphinx.console.coordinator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.exception.SqlApplicationException;
import ru.skuptsov.sphinx.console.coordinator.jmx.JmxService;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.Distributed;
import ru.skuptsov.sphinx.console.coordinator.task.ParallelSubFlowTask;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskService;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskServiceStrategyFactory;
import ru.skuptsov.sphinx.console.spring.service.api.*;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;
import ru.skuptsov.sphinx.console.coordinator.util.DeepCopyService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Andrey on 11.08.2014.
 */
@ControllerAdvice
public class AbstractCoordinatorController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * По умолчанию при создании коллекции создаётся одна реплика с номером
     */
	public static final Long FIRST_REPLICA = 1L;
	
	public static final Long SINGLE_REPLICA = 1L;

    @Autowired
    protected TasksMapService tasksMapService;
    /**
	 * Информация о параметрах коллекции
	 * inject from {@link ru.skuptsov.sphinx.console.coordinator.task.schedule.info.CollectionsInfoService#collectionsInfoMap}
	 */
    @Resource
    protected ConcurrentHashMap<String, CollectionInfoWrapper> collectionsInfoMap;
    
    @Resource
    protected ConcurrentHashMap<String, ServerInfoWrapper> serversInfoMap;
    
    @Resource
    protected ConcurrentHashMap<String, SnippetInfoWrapper> snippetsInfoMap;
    
    @Autowired
    protected TaskServiceStrategyFactory<Task> taskServiceStrategyFactory;
    
    @Autowired
	protected ServerService serverService;
    
    @Autowired
	protected ProcessService processService;
    
    @Autowired
    protected ConfigurationFieldsService configurationFieldsService;

    @Autowired
    protected CollectionService collectionService;

	@Autowired
	protected DeepCopyService deepCopyService;
	
	@Autowired
    protected ReplicaService replicaService;

    @Autowired
    protected AdminProcessService adminProcessService;

    @Autowired
    protected ConfigurationService configurationService;
    
    @Autowired
    protected SnippetConfigurationService snippetConfigurationService;
    
    @Autowired
    protected FieldMappingService fieldMappingService;

    @Autowired
    protected JmxService jmxService;

    protected TaskService<Task> getTaskService(TaskName taskName) {
    	return taskServiceStrategyFactory.getTaskService(taskName.getTitle());
    }

    public Status execute(Task task, boolean emptySphinxProcesses) {
        return execute(task, null, emptySphinxProcesses);
    }
    
    public Status execute(Task task, String executeServerName, boolean emptySphinxProcesses) {
    	logger.info("START EXECUTING TASK, UID: " + task.getTaskUID());
    	logger.info("EXECUTE SERVER NAME: " + executeServerName);

        jmxService.setTaskCoordinatorAddress(task);

    	if (emptySphinxProcesses) {
    		AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, task.getSearchServerName());
    		if (searchAdminProcess != null) {
                jmxService.setTaskAgentAddress(task, searchAdminProcess);
    		} else {
                jmxService.setTaskSearchAgentLocalAddress(task);
    		}
    		AdminProcess indexAdminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, task.getIndexServerName());
    		if (indexAdminProcess != null) {
                jmxService.setTaskAgentAddress(task, indexAdminProcess);
    		} else {
                jmxService.setTaskIndexAgentLocalAddress(task);
    		}

    	} else {
            SphinxProcess searchSphinxProcess;
            if(executeServerName != null && executeServerName.length() > 0) {
    		    searchSphinxProcess = processService.findByCollectionNameAndType(executeServerName, task.getCollectionName(), SphinxProcessType.SEARCHING).get(0); //TODO
            } else {
                searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), task.getReplicaNumber());
            }
            
            logger.info("SEARCH SPHINX PROCESS: " + searchSphinxProcess);
    		
    		if (searchSphinxProcess != null) {
    			Server server = searchSphinxProcess.getServer();
    			if (server != null) {
    				AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
    				if (searchAdminProcess != null) {
                        jmxService.setTaskAgentAddress(task, searchAdminProcess);
                        if(executeServerName == null || executeServerName.length() == 0) {
                            task.setSearchServer(server);
                        }
    				} else {
                        jmxService.setTaskSearchAgentLocalAddress(task);
    				}
    			}
    		}

            SphinxProcess indexSphinxProcess;
            if(executeServerName != null && executeServerName.length() > 0) {
                indexSphinxProcess = processService.findByCollectionNameAndType(executeServerName, task.getCollectionName(), SphinxProcessType.INDEXING).get(0); //TODO
            } else {
                indexSphinxProcess = processService.findIndexingProcess(task.getCollectionName()); //TODO
            }
            
            logger.info("INDEX SPHINX PROCESS: " + indexSphinxProcess);

            if (indexSphinxProcess != null) {
    			Server server = indexSphinxProcess.getServer();
    			if (server != null) {
    				AdminProcess indexAdminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, server);
    				if (indexAdminProcess != null) {
                        jmxService.setTaskAgentAddress(task, indexAdminProcess);
                        if(executeServerName == null || executeServerName.length() == 0) {
    	    		        task.setIndexServer(server);
                        }
    				} else {
                        jmxService.setTaskIndexAgentLocalAddress(task);
    				}
    			}
    		}
    	}
    	
    	if (task.getSearchAgentAddress() == null) {
            jmxService.setTaskSearchAgentLocalAddress(task);
    	}
    	
    	if (task.getIndexAgentAddress() == null) {
            jmxService.setTaskIndexAgentLocalAddress(task);
    	}

        logger.info("TASK COLLECTION NAME: " + task.getCollectionName());
        logger.info("TASK COORDINATOR AGENT ADDRESS: " + task.getCoordinatorAddress());
        logger.info("TASK INDEX AGENT ADDRESS: " + task.getIndexAgentAddress());
        logger.info("TASK SEARCH AGENT ADDRESS: " + task.getSearchAgentAddress());

        Status putTaskStatus = tasksMapService.putTask(task);
        if(putTaskStatus.getCode() != Status.SUCCESS_CODE){
            return putTaskStatus;
        }
		
		logger.info("TASK STATE: " + task.getState());
		
		if (!emptySphinxProcesses && task instanceof ParallelSubFlowTask) {
            Status createSubTasksStatus = createSubTasks(task, getReplicas(task.getCollectionName()), task.getClass());
            if(createSubTasksStatus.getCode() != Status.SUCCESS_CODE){
                return createSubTasksStatus;
            }
		}

        if(task instanceof ReplicaLoopTask){
            ((ReplicaLoopTask)task).initReplicaLoop(initReplicas(task.getCollectionName()));
        }

        task.setStartDate(new Date());

        Status status = getTaskService(task.getTaskName()).execute(task);
		
		logger.info("TASK STATE: " + task.getState());
		
		if (status != null && status.getCode() != Status.SUCCESS_CODE) {
			task.setStatus(status);
			return status;
		}

		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }
    
    @ExceptionHandler({NoHandlerFoundException.class})
	@ResponseBody
	public Status requestHandlingNoHandlerFound(NoHandlerFoundException e) {
        logger.error("ERROR OCCURED", e);
    	return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.BAD_REST_REQUEST, e.getMessage());
	}
    
    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class})
	@ResponseBody
	public Status resolveMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException e) {
        logger.error("ERROR OCCURED", e);
    	return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.BAD_REST_REQUEST, e.getMessage());
	}
	
	@ExceptionHandler({ApplicationException.class})
	@ResponseBody
	public Status resolveDataIntegrityException(ApplicationException e) {
		logger.error("ERROR OCCURED", e);
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.DATA_INTEGRITY_FAILURE, e.getMessage());
	}

    @ExceptionHandler({SqlApplicationException.class})
    @ResponseBody
    public Status resolveSqlDataIntegrityException(SqlApplicationException e) {
        logger.error("ERROR OCCURED", e);
        Status status = new Status(SystemInterface.COORDINATOR_CONFIGURATION);
        status.setCode(StatusCode.DATA_INTEGRITY_FAILURE.getCode());
        status.setMessage(StatusCode.DATA_INTEGRITY_FAILURE.getTitle());
        status.setDescription(e.getDescription());
        status.setStackTrace(e.getMessage());
        return status;
    }
	
	@ExceptionHandler({javax.naming.ServiceUnavailableException.class})
	@ResponseBody
	public Status resolveAgentServiceUnavailableException(javax.naming.ServiceUnavailableException e) {
		logger.error("ERROR OCCURED", e);
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.AGENT_SERVICE_UNAVAILABLE, e.getMessage());
	}
	
	@ExceptionHandler({org.springframework.jmx.MBeanServerNotFoundException.class})
	@ResponseBody
	public Status resolveMBeanServerNotFoundException(org.springframework.jmx.MBeanServerNotFoundException e) {
		logger.error("ERROR OCCURED", e);
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.AGENT_SERVICE_UNAVAILABLE, e.getMessage());
	}
	
	protected List<Replica> getReplicas(String collectionName) {
		    Set<Replica> replicas = collectionService.getCollection(collectionName).getReplicas();
		    if (replicas != null) {
		        return new LinkedList<Replica>(replicas);
		    }
		    return null;
    }

    protected Status createSubTasks(Task task, List<Replica> replicas, Class<? extends Task> T) {
        List<Task> subTasks = new ArrayList<Task>();

        for (Replica replica : replicas) {
            Task subTask = deepCopyService.deepCopy(task, T);
            subTask.setState(task.getSubflowChain().getFirstState());
            subTask.setParent(task);

            subTask.setIndexAgentAddress(task.getIndexAgentAddress());
            subTask.setIndexServer(task.getIndexServer());
            subTask.setCoordinatorAddress(task.getCoordinatorAddress());
            subTask.setStatus(Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID()));

            subTask.setReplicaNumber(replica.getNumber());
            subTask.setTaskUID(UUID.randomUUID().toString());
            subTask.setParentTaskUID(task.getTaskUID());
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), replica.getNumber());
            if (searchSphinxProcess != null) {
                Server server = searchSphinxProcess.getServer();
                if (server != null) {
                    subTask.setSearchServer(server);
                    AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
                    if (searchAdminProcess != null) {
                        jmxService.setTaskAgentAddress(subTask, searchAdminProcess);
                    }
                }
            }
            Status putTaskStatus = tasksMapService.putTask(subTask);
            if (putTaskStatus.getCode() != Status.SUCCESS_CODE) {
                return putTaskStatus;
            } else {
                subTasks.add(subTask);
            }
        }

        task.setSubTasks(subTasks);

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);

    }

    protected SearchConfigurationPortWrapper getSearchConfigurationPortWrapper(Long configurationId){
        SearchConfigurationPortWrapper port = null;
        ConfigurationFields searchConfigurationPortField = configurationFieldsService.getSearchPort(configurationId);
        if(searchConfigurationPortField != null){
            port = new SearchConfigurationPortWrapper(Integer.valueOf(searchConfigurationPortField.getFieldValue()));
        }
        return port;
    }
	
	protected DistributedConfigurationPortWrapper getDistributedConfigurationPortWrapper(Long configurationId){
		DistributedConfigurationPortWrapper port = null;
        ConfigurationFields distributedConfigurationPortField = configurationFieldsService.getDistributedPort(configurationId);
        if(distributedConfigurationPortField != null){
            port = new DistributedConfigurationPortWrapper(Integer.valueOf(distributedConfigurationPortField.getFieldValue()));
        }
        return port;
    }

    protected boolean checkIfMappingChanged(Configuration configuration) {
        Configuration oldConfiguration = configurationService.findById(configuration.getId());
        if(oldConfiguration.getId() != null) {
            Set<FieldMapping> newFieldMappings = configuration.getFieldMappings();
            Set<FieldMapping> oldFieldMappings = configurationService.findById(configuration.getId()).getFieldMappings();
            return !oldFieldMappings.equals(newFieldMappings);
        } else {
            return false;
        }
    }

    private List<Replica> initReplicas(String collectionName) {
        List<Replica> replicas = replicaService.findByCollectionName(collectionName);
        for (Replica replica : replicas) {
            initReplicaSearchAgent(replica);
        }

        return replicas;
    }

    private void initReplicaSearchAgent(Replica replica) {
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(replica.getCollection().getName(), replica.getNumber());
        if (searchSphinxProcess != null) {
            Server server = searchSphinxProcess.getServer();
            if (server != null) {
                replica.setSearchProcess(searchSphinxProcess);
                AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
                if (searchAdminProcess != null) {
                    jmxService.setReplicaAgentAddress(replica, searchAdminProcess);
                }
            }
        }
    }

    protected void convertNodes(Distributed task, DistributedCollectionWrapper distributedCollectionWrapper) {
    	List<SimpleCollectionWrapper> simpleCollectionWrappers = distributedCollectionWrapper.getNodes();
    	
    	if (simpleCollectionWrappers != null) {
    		for (SimpleCollectionWrapper simpleCollectionWrapper : simpleCollectionWrappers) {
    			DistributedCollectionNode node = new DistributedCollectionNode();
    			node.setSimpleCollection(collectionService.getCollection(simpleCollectionWrapper.getCollectionName()));
    			node.setDistributedCollection(distributedCollectionWrapper.getCollection());
    			
    			
    			List<SimpleCollectionReplicaWrapper> simpleCollectionReplicaWrappers = simpleCollectionWrapper.getAgents();
    			
    			if (simpleCollectionReplicaWrappers != null) {
    				for (SimpleCollectionReplicaWrapper simpleCollectionReplicaWrapper : simpleCollectionReplicaWrappers) {
    					DistributedCollectionAgent agent = new DistributedCollectionAgent();
    					
    					agent.setDistributedCollectionNode(node);
                        SphinxProcess sphinxProcess = processService.findSearchProcess(simpleCollectionReplicaWrapper.getNodeHost(), simpleCollectionReplicaWrapper.getNodeDistribPort());
    					agent.setSphinxProcess(sphinxProcess);
    					agent.setNodeDistribPort(simpleCollectionReplicaWrapper.getNodeDistribPort());
    					agent.setNodeHost(simpleCollectionReplicaWrapper.getNodeHost());
    					
    					node.getDistributedCollectionAgents().add(agent);
    				}
    			}
    			
    			task.addNode(node);
    			
    		}
    		
    		distributedCollectionWrapper.getCollection().setDistributedCollectionNodes(task.getNodes());
    	}
    }
    
    protected void reformNodes(Set<DistributedCollectionNode> nodes) {
		for (DistributedCollectionNode node : nodes) {
			Collection simpleCollection = node.getSimpleCollection();
			node.getDistributedCollectionAgents().clear();
			
			Set<Replica> replicas = simpleCollection.getReplicas();
			
			for (Replica replica : replicas) {
				logger.info("REPLICA: " + replica.getId() + ", " + replica.getNumber());
				DistributedCollectionAgent agent = new DistributedCollectionAgent();
				agent.setDistributedCollectionNode(node);
				SphinxProcess searchProcess = processService.findSearchProcess(node.getSimpleCollection().getName(), replica.getNumber());
				agent.setSphinxProcess(searchProcess);
				logger.info("SPHINX PROCESS: " + searchProcess);
				logger.info("SERVER: " + searchProcess.getServer());
				
				
				agent.setNodeHost(searchProcess.getServer().getIp());
				agent.setNodeDistribPort(new Integer(searchProcess.getConfiguration().getDistributedListenPort()));
				
				node.getDistributedCollectionAgents().add(agent);
			}
		}
	}
    
}
