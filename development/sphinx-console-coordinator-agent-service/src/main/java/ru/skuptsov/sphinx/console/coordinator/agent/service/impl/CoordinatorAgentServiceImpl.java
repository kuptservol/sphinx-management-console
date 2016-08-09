package ru.skuptsov.sphinx.console.coordinator.agent.service.impl;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.agent.command.*;
import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.MakeCollectionFullRebuildApplyTask;

@Component
@ManagedResource(objectName = CoordinatorAgentServiceImpl.MBEAN_NAME,  description = "") 
public class CoordinatorAgentServiceImpl implements CoordinatorAgentService {
	
	private static final Logger logger = LoggerFactory.getLogger(CoordinatorAgentServiceImpl.class);
	
	public static final String MBEAN_NAME = "coordinator.agent.mbeans:type=config,name=CoordinatorAgentService";
	
	public static final Long FIRST_REPLICA = 1L;

	@Autowired
	private org.springframework.jmx.access.MBeanProxyFactoryBean coordinatorCallbackServiceClient;
	
	@Autowired
	private InitCommandService initCommandService;
    @Autowired
    private CopyFilesCommandService copyFilesCommandService;
    @Autowired
    private DeleteFoldersCommandService deleteFoldersCommandService;
    @Autowired
    private DeleteIndexDataFilesService deleteIndexDataFilesService;
    @Autowired
    private CleaningIndexDataFolderService cleaningIndexDataFolderService;
    @Autowired
    private DeleteProcessCommandService deleteProcessCommandService;
    @Autowired
    private IndexingCommandService indexingCommandService;
    @Autowired
    private RotatingCommandService rotatingCommandService;
    @Autowired
    private StartCommandService startCommandService;
    @Autowired
    private StopCommandService stopCommandService;
    @Autowired
    private UpdateConfigCommandService updateConfigCommandService;
    @Autowired
    private MoveFilesCommandService moveFilesCommandService;
    @Autowired
    private StopIndexingCommandService stopIndexingCommandService;
    @Autowired
    private StartMergingCommandService startMergingCommandService;
    @Autowired
    private DeleteSnippetFoldersCommandService deleteSnippetFoldersCommandService;
    @Autowired
    private RunSnippetQueryCommandService runSnippetQueryCommandService;
    @Autowired
    private RunRSyncCommandService runRSyncCommandService;
    @Autowired
    private StopSnippetQueryCommandService stopSnippetQueryCommandService;
    @Autowired
    private AddProcessToStartupCommandService addProcessToStartupCommandService;
    @Autowired
    private RemoveProcessFromStartupCommandService removeProcessFromStartupCommandService;


    @ManagedOperation(description = "")
	public String test() {
	    return "success";
	}

	@Override
	@ManagedOperation(description = "")  
	public Status setCoordinator(Task task) {
		logger.info("ABOUT TO SET COORDINATOR CALLBACK ADDRESS: " + task.getCoordinatorAddress());
		try {
			
			coordinatorCallbackServiceClient.setServiceUrl(task.getCoordinatorAddress());
			
		} catch (MalformedURLException e) {
            logger.error("Error during setCoordinator", e);
			return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.BAD_COORDINATOR_CALLBACK_ADDRESS, task.getTaskUID(), e);
		}
		
		return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
    @ManagedOperation(description = "")
    public Status startRotating(Task task, String processName) {
        logger.info("ABOUT TO ROTATING: " + processName);

        rotatingCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
	@ManagedOperation(description = "") 
	public Status startCreatingProcess(Task task, String processName, String sphinxServiceContent) {
		logger.info("ABOUT TO START CREATING PROCESS: " + processName);
		
        initCommandService.execute(task, processName, sphinxServiceContent);

		return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

    @Override
    @ManagedOperation(description = "")
    public Status addProcessToStartup(Task task, String processName) {
        logger.info("ABOUT TO ADD PROCESS TO STARTUP: " + processName);

        addProcessToStartupCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

    @Override
    @ManagedOperation(description = "")
    public Status removeProcessFromStartup(Task task, String processName) {
        logger.info("ABOUT TO REMOVE PROCESS FROM STARTUP: " + processName);

        removeProcessFromStartupCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

	@Override
    @ManagedOperation(description = "")
    public Status startIndexing(Task task, String processName) {
        logger.info("ABOUT TO INDEXING: " + processName + ", INDEX TYPE: " + task.getIndexType().getTitle());

        indexingCommandService.execute(task, processName, task.getCollectionName(), task.getIndexType().getTitle());

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
    @ManagedOperation(description = "")
	public Status startUpdatingConfig(Task task, String processName, String configContent) {
        logger.info("ABOUT TO UPDATE CONFIG: " + processName);

        updateConfigCommandService.execute(task, processName, configContent);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
    @ManagedOperation(description = "")
    public Status startPushingFiles(Task task, String processName) {
        logger.info("integration test startPushingFiles set servers");
        String indexFileName = task.getCollectionName();
        String fromServer = task.getIndexServer().getIp();
        String toServer = task.getSearchServer().getIp();
        
        if (task.getSphinxProcessType() == SphinxProcessType.INDEXING) {
        	toServer = fromServer;
        }
        
        logger.info("integration test startPushingFiles servers: from - " + fromServer + ", to - " +  toServer);

        logger.info("integration test startPushingFiles set processes");
        String fromProcessName = task instanceof MakeCollectionFullRebuildApplyTask ? ((MakeCollectionFullRebuildApplyTask)task).getCollectionFullRebuildName() : processName;
        logger.info("IS PUSH INDEX FILES FOR REPLICA: " + task.isPushIndexFilesForReplica());
        if (task.isPushIndexFilesForReplica()) {
        	fromProcessName = task.getCollectionName() + "_" + FIRST_REPLICA;
        }
        String toProcessName = processName;
        logger.info("integration test startPushingFiles processes: from - " + fromProcessName + ", to - " +  toProcessName);

        logger.info("integration test startPushingFiles set dirs");
        String fromDir = "data/indexing/" + fromProcessName + "/";
        String toDir = "data/"+(task.getSphinxProcessType() == SphinxProcessType.INDEXING ? "indexing" : "searching") + "/" + toProcessName + "/";
        logger.info("integration test startPushingFiles dirs: from - " + fromDir + ", to - " +  toDir);

        Boolean isStrictCopy = task.getStrictCopy();
        logger.info("IS STRICT COPY: " + isStrictCopy);
        logger.info("ABOUT TO PUSHING FILES: " + processName + " FROM : " + fromServer + " " + fromDir + " TO: " + toServer + " " + toDir);

        copyFilesCommandService.execute(task, processName, indexFileName, fromServer, toServer, fromDir, toDir, task.getIndexType(), isStrictCopy);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

	@Override
    @ManagedOperation(description = "")
    public Status startDeletingProcess(Task task, String processName) {
        logger.info("ABOUT TO DELETE PROCESS: " + processName);

        deleteProcessCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
    @ManagedOperation(description = "")
    public Status stopProcess(Task task, String processName) {
        logger.info("ABOUT TO STOP PROCESS: " + processName);

        stopCommandService.execute(task, processName, false);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

    @Override
    @ManagedOperation(description = "")
    public Status stopProcessIgnoreFailure(Task task, String processName) {
        logger.info("ABOUT TO STOP PROCESS IGNORE FAILURE: " + processName);

        stopCommandService.execute(task, processName, true);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

    @Override
    @ManagedOperation(description = "")
    public Status startProcess(Task task, String processName) {
        logger.info("ABOUT TO START PROCESS: " + processName);

        startCommandService.execute(task, processName, task.getCollectionName(), task.getIndexType().getTitle());

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

	@Override
    @ManagedOperation(description = "")
    public Status startDeletingIndexData(Task task, String processName) {
        logger.info("ABOUT TO DELETE FOLDERS: " + processName);

        deleteFoldersCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

    @Override
    @ManagedOperation(description = "")
    public Status startDeleteIndexDataFiles(Task task, String processName) {
        logger.info("ABOUT TO DELETE INDEX DATA FILES: " + processName);

        deleteIndexDataFilesService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

    @Override
    @ManagedOperation(description = "")
    public Status startCleaningIndexDataFolder(Task task, String processName) {
        logger.info("ABOUT TO DELETE FOLDERS: " + processName);

        cleaningIndexDataFolderService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

    @Override
    @ManagedOperation(description = "")
    public Status moveFiles(Task task, String processName, String fromServer, String toServer) {
        logger.info("ABOUT TO MOVE FILES: " + processName);

        moveFilesCommandService.execute(task, processName, fromServer, toServer);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
    }

    @Override
    @ManagedOperation(description = "")
    public Status stopIndexing(Task task, String processName) {
        logger.info("ABOUT TO STOP INDEXING: " + processName);

        stopIndexingCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
	@ManagedOperation(description = "")
	public Status startMerging(Task task) {
		logger.info("ABOUT TO START MERGING: " + task.getProcessName());
		
		startMergingCommandService.execute(task, task.getProcessName(), task.getCollectionName(), task.getMergeOption());
		
		return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	
	
	@Override
    @ManagedOperation(description = "")
    public Status startDeletingSnippetData(Task task, String processName) {
        logger.info("ABOUT TO DELETE SNIPPET FOLDERS: " + processName);

        deleteSnippetFoldersCommandService.execute(task, processName);

        return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
	@ManagedOperation(description = "")
	public Status runSnippetQuery(Task task, SnippetConfiguration configuration) {
		runSnippetQueryCommandService.execute(task, task.getProcessName(), configuration, task.isSnippetFullRebuild(), task.getTaskUID());
		
		return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
	@ManagedOperation(description = "")
	public Status runRSyncCommand(Task task, String processName) {
		String fromServer = task.getIndexServer().getIp();
        String toServer = task.getSearchServer().getIp();
        String fromProcessName = task.getCollectionName() + "_" + FIRST_REPLICA;
        String toProcessName = processName;
        
        String fromDir = "snippet/indexing/" + fromProcessName + "/";
        String toDir = "snippet/searching" + "/" + toProcessName + "/";
        logger.info("rsync snippets, dirs: from - " + fromDir + ", to - " +  toDir);

		
		runRSyncCommandService.execute(task, task.getProcessName(), fromServer, toServer, fromDir, toDir);
		
		return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
	@ManagedOperation(description = "")
	public Status stopSnippetQuery(Task task) {
		stopSnippetQueryCommandService.execute(task, task.getProcessName(), task.getTaskUID());
		return Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}


}
