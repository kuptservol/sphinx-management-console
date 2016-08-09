package ru.skuptsov.sphinx.console.coordinator.monitoring;


import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.CoordinatorAgentMonitoringService;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.jmx.JmxService;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;
import ru.skuptsov.sphinx.console.coordinator.task.FullRebuildSnippetTask;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

@Component("monitoringService")
@Scope("prototype")
public class MonitoringService {
	private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
	
	private static final int ALLOWED_CONNECTIONS_IN_POOL_FOR_PARTICULAR_AGENT = 1;

	private org.springframework.jmx.access.MBeanProxyFactoryBean coordinatorAgentMonitoringServiceClient;
	
	@Autowired
	@Qualifier("agentMonitoringClientsPool")
    private GenericKeyedObjectPool<String, org.springframework.jmx.access.MBeanProxyFactoryBean> agentMonitoringClientsPool;
	
	@Autowired
	private ServerService serverService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private JmxService jmxService;
    
    @Resource
    protected TasksMapService tasksMapService;
    
    private void getConnectionFromPool(String serviceUrl) {
    	long active = agentMonitoringClientsPool.getNumActive(serviceUrl);
    	long idle = agentMonitoringClientsPool.getNumIdle(serviceUrl);
    	
    	logger.debug("NUM ACTIVE: " + active);
    	logger.debug("NUM IDLE: " + idle);
    	
    
    	try {
    			
    		coordinatorAgentMonitoringServiceClient = (MBeanProxyFactoryBean) agentMonitoringClientsPool.borrowObject(serviceUrl);	
        	
    		active = agentMonitoringClientsPool.getNumActive(serviceUrl);
    		
    		logger.debug("NUM ACTIVE AFTER BORROW: " + active);
    		
    		if (active >= ALLOWED_CONNECTIONS_IN_POOL_FOR_PARTICULAR_AGENT) {
    			agentMonitoringClientsPool.returnObject(serviceUrl, coordinatorAgentMonitoringServiceClient);		
    		}
    		
    		logger.debug("INSTANCE OF coordinatorAgentMonitoringServiceClient: " + coordinatorAgentMonitoringServiceClient);
    		
		} catch (Exception e) {
            logger.error("Error during getConnectionFromPool for monitoring service", e);
			throw new ApplicationException(e);
		}
    }

    private void initCoordinatorAgentMonitoringServiceClient(String ip, Integer port){
        try {
            String serviceUrl = jmxService.getConnectorUrl(ip, port, ProcessType.SEARCH_AGENT);
            getConnectionFromPool(serviceUrl);
        } catch (Exception e) {
            logger.error("Error during initCoordinatorAgentMonitoringServiceClient", e);
            throw new ApplicationException(e);
        }
    }

	public ProcessStatus isProcessAlive(String ip, Integer port, String processName) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
		CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();
		
		return coordinatorAgentMonitoringService.isProcessAlive(processName);
	}
	
	public Boolean runQuery(String ip, Integer port, Integer searchdMySQLPort, String collectionName) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();
		
		return coordinatorAgentMonitoringService.runQuery(searchdMySQLPort, collectionName);
	}
	
	public Long getCollectionSize(String ip, Integer port, Integer searchdMySQLPort, String collectionName) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();
		
		return coordinatorAgentMonitoringService.getCollectionSize(port, searchdMySQLPort, collectionName);
	}
	
	public Boolean isCurrentlyIndexing(String ip, Integer port, String processName) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();
	
		return coordinatorAgentMonitoringService.isCurrentlyIndexing(processName);
	}

    public Boolean isCurrentlyIndexing(String ip, Integer port, String processName, String indexName) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();

        return coordinatorAgentMonitoringService.isCurrentlyIndexing(processName, indexName);
    }

    public Boolean isCurrentlyMerging(String ip, Integer port, String processName) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();

        return coordinatorAgentMonitoringService.isCurrentlyMerging(processName);
    }

    public byte[] getRealSphinxConf(String collectionName, Long replicaNumber) {
        logger.info("ABOUT TO RETRIEVE REAL SPHINX CONF CONTENT, FOR: " + collectionName + " Replica number: " + replicaNumber);
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, replicaNumber);
        if (searchSphinxProcess == null) {
        	return "".getBytes();
        }
        Server server = searchSphinxProcess.getServer();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);

        String dirName = collectionName + "_" + replicaNumber;

        byte[] content = null;
        try {

            initCoordinatorAgentMonitoringServiceClient(server.getIp(), adminProcess.getPort());
            CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();
            content = coordinatorAgentMonitoringService.getRealSphinxConf(dirName);

        } catch(Exception e) {
            content = null;
            logger.error("Error during retrieving real sphinx.conf file: ", e);
        }
        return content;
    }
    
    public byte[] getSphinxLog(String collectionName, Long replicaNumber, Long recordNumber) {
        logger.info("ABOUT TO RETRIEVE REAL SPHINX LOG CONTENT, FOR: " + collectionName + " Replica number: " + replicaNumber);
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, replicaNumber);
        if (searchSphinxProcess == null) {
        	return "".getBytes();
        }
        Server server = searchSphinxProcess.getServer();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);

        String dirName = collectionName + "_" + replicaNumber;

        byte[] content = null;
        try {

            initCoordinatorAgentMonitoringServiceClient(server.getIp(), adminProcess.getPort());
            CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();
            content = coordinatorAgentMonitoringService.getSphinxLog(dirName, recordNumber);

        } catch(Exception e) {
            content = null;
            logger.error("Error during retrieving real sphinx.conf file: ", e);
        }
        return content;
    }

    public boolean canExecuteIndexing(String collectionName, Long replicaNumber) {
		logger.info("ABOUT TO CHECK IF INDEXING CAN BE EXECUTED, FOR: " + collectionName);
		
		String processName = collectionName + "_" + replicaNumber; 
		
		SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, replicaNumber);
		
		logger.info("PROCESS: " + searchSphinxProcess);
		
		if (searchSphinxProcess == null) {
    		return false;
    	}
		
        Server server = searchSphinxProcess.getServer();
        
        logger.info("SERVER: " + server);
        
        if (server == null) {
        	return false;
        }
        
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
        
        logger.info("ADMIN PROCESS: " + adminProcess);
        
        if (adminProcess == null) {
        	return false;
        }
        
        
        if (!isCurrentlyIndexing(server.getIp(), adminProcess.getPort(), processName)) {
        	return true;
        }
        
        return false;
	
	}

    public SphinxQLMultiResult getSphinxQLConsoleResult(String serverName, Integer searchdPort, String query) {
        Server server = serverService.getServer(serverName);
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
        initCoordinatorAgentMonitoringServiceClient(server.getIp(), adminProcess.getPort());
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();

        SphinxQLMultiResult result = coordinatorAgentMonitoringService.getSphinxQLConsoleResult(searchdPort, query);
        return result;

    }
    
    public boolean isCurrentlyFullRebuildSnippet(String collectionName) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(tasksMapService.getAllTasks());
		
		for (Task task : tasks) {
		    if (task instanceof FullRebuildSnippetTask && task.getCollectionName() != null && task.getCollectionName().equals(collectionName)) {
		        return task.getTaskStatus() == TaskStatus.RUNNING;	
		    }
		}
		
		return false;
		
	}
    
    public boolean isCurrentlyRebuildSnippet(String collectionName) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(tasksMapService.getAllTasks());
		
		for (Task task : tasks) {
		    if (task instanceof RebuildSnippetsTask && task.getCollectionName() != null && task.getCollectionName().equals(collectionName)) {
		        return task.getTaskStatus() == TaskStatus.RUNNING;	
		    }
		}
		
		return false;
		
	}
    
    public Task getCurrentlyRebuildSnippetTask(String collectionName) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(tasksMapService.getAllTasks());
		
		for (Task task : tasks) {
		    if (task instanceof RebuildSnippetsTask && task.getCollectionName() != null && task.getCollectionName().equals(collectionName)
		    		&& task.getTaskStatus() == TaskStatus.RUNNING) {
		        return task;	
		    }
		}
		
		return null;
		
	}
    
    public Task getCurrentlyFullRebuildSnippetTask(String collectionName) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(tasksMapService.getAllTasks());
		
		for (Task task : tasks) {
		    if (task instanceof FullRebuildSnippetTask && task.getCollectionName() != null && task.getCollectionName().equals(collectionName)
		    		&& task.getTaskStatus() == TaskStatus.RUNNING) {
		        return task;	
		    }
		}
		
		return null;
		
	}

    public Set<SearchQuery> getSearchQueriesResults(String ip, Integer port, String collectionName, Long replicaNumber, Date lastParsedDate) {
        initCoordinatorAgentMonitoringServiceClient(ip, port);
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService =  (CoordinatorAgentMonitoringService) coordinatorAgentMonitoringServiceClient.getObject();

        Set<SearchQuery> result = coordinatorAgentMonitoringService.getSearchQueriesResults(collectionName, replicaNumber, lastParsedDate);
        return result;

    }
}
