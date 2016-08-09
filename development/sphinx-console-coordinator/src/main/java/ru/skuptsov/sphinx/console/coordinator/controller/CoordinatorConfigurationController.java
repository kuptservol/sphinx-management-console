package ru.skuptsov.sphinx.console.coordinator.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.skuptsov.sphinx.console.coordinator.annotation.ExecuteOnlyWithValidParams;
import ru.skuptsov.sphinx.console.coordinator.annotation.ExistsInDB;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.search.query.SearchQueryProcessService;
import ru.skuptsov.sphinx.console.coordinator.service.api.CoordinatorConfiguration;
import ru.skuptsov.sphinx.console.coordinator.task.*;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.info.CollectionsInfoService;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.MergeCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildCollectionTaskScheduler;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildSnippetTaskScheduler;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxConfService;
import ru.skuptsov.sphinx.console.spring.service.api.*;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import javax.validation.Valid;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;


@Controller
public class CoordinatorConfigurationController extends AbstractCoordinatorController implements CoordinatorConfiguration {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DeltaService deltaService;

    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Autowired
	private ScheduledTaskService scheduledTaskService;

	@Autowired
	private RebuildCollectionTaskScheduler rebuildCollectionTaskScheduler;
	
	@Autowired
	private MergeCollectionTaskScheduler mergeCollectionTaskScheduler;
	
	@Autowired
	private RebuildSnippetTaskScheduler rebuildSnippetTaskScheduler;

	@Autowired
    private AdminProcessService adminProcessService;
	
	@Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private ConfigurationTemplateService configurationTemplateService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private GenerateSphinxConfService generateSphinxConfService;

    @Autowired
    private CollectionsInfoService collectionsInfoService;

    @Autowired
    private SearchQueryProcessService searchQueryProcessService;


    /*TODO sphinx.console-261*/
    private Set<ConfigurationFields> initSourceConfigurationFields(Configuration ownerConfiguration, Set<ConfigurationFields> configurationFields) {
        for (ConfigurationFields configurationField : configurationFields) {
             /*TODO check other id links as GUI example*/
            configurationField.setConfiguration(ownerConfiguration);
            configurationField.setId(null);
        }

//        configurationFieldsService.deleteAllByConfigurationId(ownerConfiguration.getId());

        return configurationFields;
    }

    /*TODO sphinx.console-261*/
    private Set<ConfigurationFields> initSearchConfigurationFields(Configuration ownerConfiguration) {
        Set<ConfigurationFields> configurationFields = new HashSet<ConfigurationFields>();

        ConfigurationFields configurationField = configurationFieldsService.getSearchPort(ownerConfiguration.getId());
        if(configurationField != null) {
            configurationFields.add(configurationField);
        }

        ownerConfiguration.setSearchConfigurationFields(configurationFields);

        return configurationFields;
    }

    /*TODO sphinx.console-261*/
    private ConfigurationTemplate initConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        if(configurationTemplate != null && StringUtils.isNotEmpty(configurationTemplate.getName())) {
            ConfigurationTemplate savedConfigurationTemplate =
                    configurationTemplateService.getConfigurationTemplate(configurationTemplate.getName(), configurationTemplate.getType(), configurationTemplate.getCollectionType());
            if(savedConfigurationTemplate != null){
                configurationTemplate = savedConfigurationTemplate;
            }
            else{
                for (ConfigurationFields configurationFields : configurationTemplate.getConfigurationFields()) {
                    configurationFields.setConfigurationTemplate(configurationTemplate);
                }
            }
        }

        return configurationTemplate;
    }

    /*TODO sphinx.console-261*/
    private Configuration initConfiguration(Configuration configuration) {
        if(configuration.getName() != null) {
            Configuration savedConfiguration =
                    configurationService.getConfiguration(configuration.getName());
            if(savedConfiguration != null) {
                configuration.setId(savedConfiguration.getId());
            } else {
                configuration.setId(null);
                configuration.setName(null);
            }

            for (FieldMapping fieldMapping : configuration.getFieldMappings()) {
                fieldMapping.setConfiguration(configuration);
                fieldMapping.setId(null);
            }

            initSourceConfigurationFields(configuration, configuration.getSourceConfigurationFields());
            initSearchConfigurationFields(configuration);
        }

        if(configuration.getDatasource() != null) {
            DataSource savedDataSource = dataSourceService.getDataSource(configuration.getDatasource().getName());
            if (savedDataSource != null) {
                configuration.getDatasource().setId(savedDataSource.getId());
            } else {
                configuration.getDatasource().setId(null);
            }
        }

        ConfigurationTemplate configurationTemplate;
        configurationTemplate = initConfigurationTemplate(configuration.getConfigurationTemplate());
        configuration.setConfigurationTemplate(configurationTemplate);
        configurationTemplate = initConfigurationTemplate(configuration.getSearchConfigurationTemplate());
        configuration.setSearchConfigurationTemplate(configurationTemplate);
        configurationTemplate = initConfigurationTemplate(configuration.getIndexerConfigurationTemplate());
        configuration.setIndexerConfigurationTemplate(configurationTemplate);

        return configuration;
    }

    /*TODO sphinx.console-261*/
    private CollectionWrapper initCollectionWrapper(CollectionWrapper collectionWrapper) {
        Collection savedCollection =
                collectionService.getCollection(collectionWrapper.getCollection().getName());
        if(savedCollection != null) {
            collectionWrapper.getCollection().setId(savedCollection.getId());
        } else {
            collectionWrapper.getCollection().setId(null);
        }

        if(collectionWrapper.getCollection().getDelta() != null) {
            Delta savedDelta = deltaService.getByCollectionName(collectionWrapper.getCollection().getName());
            if (savedDelta != null) {
                collectionWrapper.getCollection().getDelta().setId(savedDelta.getId());
            } else {
                collectionWrapper.getCollection().getDelta().setId(null);
            }
            if (collectionWrapper.getCollection().getDelta().getExternalAction() != null) {
                if (savedDelta != null && savedDelta.getExternalAction() != null) {
                    collectionWrapper.getCollection().getDelta().getExternalAction().setId(savedDelta.getExternalAction().getId());
                    DataSource savedDataSource = dataSourceService.getDataSource(collectionWrapper.getCollection().getDelta().getExternalAction().getDataSource().getName());
                    if (savedDataSource != null) {
                        collectionWrapper.getCollection().getDelta().getExternalAction().getDataSource().setId(savedDataSource.getId());
                    }
                } else {
                    collectionWrapper.getCollection().getDelta().getExternalAction().setId(null);
                }
            }
        }

        if(collectionWrapper.getCollection().getReplicas() != null) {
            for (Replica replica : collectionWrapper.getCollection().getReplicas()) {
                Replica savedReplica = replicaService.findReplicaByNumber(collectionWrapper.getCollection().getName(),
                        replica.getNumber());
                if (savedReplica != null) {
                    replica.setId(savedReplica.getId());
                } else {
                    replica.setId(null);
                }
            }
        }


        initConfiguration(collectionWrapper.getIndexConfiguration());
        initConfiguration(collectionWrapper.getSearchConfiguration());

        //init others

        return collectionWrapper;
    }

    private SnippetConfigurationWrapper initSnippetWrapper(SnippetConfigurationWrapper snippetConfigurationWrapper) {
        SnippetConfiguration savedSnippet = snippetConfigurationService.getSnippet(snippetConfigurationWrapper.getCollectionName());
        if(savedSnippet != null) {
            snippetConfigurationWrapper.getSnippetConfiguration().setId(savedSnippet.getId());
            if(snippetConfigurationWrapper.getSnippetConfiguration().getFields() != null) {
                for (SnippetConfigurationField snippetConfigurationField : snippetConfigurationWrapper.getSnippetConfiguration().getFields()) {
                    snippetConfigurationField.setId(null);
                }
            }
        } else if(snippetConfigurationWrapper.getSnippetConfiguration() != null) {
            snippetConfigurationWrapper.getSnippetConfiguration().setId(null);
        }

        return snippetConfigurationWrapper;
    }

    /*TODO sphinx.console-261*/
    private DistributedCollectionWrapper initCollectionWrapper(DistributedCollectionWrapper collectionWrapper) {
        Collection savedCollection =
                collectionService.getCollection(collectionWrapper.getCollection().getName());
        if(savedCollection != null) {
            collectionWrapper.getCollection().setId(savedCollection.getId());
        } else {
            collectionWrapper.getCollection().setId(null);
        }

        if(collectionWrapper.getCollection().getDelta() != null) {
            Delta savedDelta = deltaService.getByCollectionName(collectionWrapper.getCollection().getName());
            if (savedDelta != null) {
                collectionWrapper.getCollection().getDelta().setId(savedDelta.getId());
            } else {
                collectionWrapper.getCollection().getDelta().setId(null);
            }
            if (collectionWrapper.getCollection().getDelta().getExternalAction() != null) {
                if (savedDelta != null && savedDelta.getExternalAction() != null) {
                    collectionWrapper.getCollection().getDelta().getExternalAction().setId(savedDelta.getExternalAction().getId());
                    DataSource savedDataSource = dataSourceService.getDataSource(collectionWrapper.getCollection().getDelta().getExternalAction().getDataSource().getName());
                    if (savedDataSource != null) {
                        collectionWrapper.getCollection().getDelta().getExternalAction().getDataSource().setId(savedDataSource.getId());
                    }
                } else {
                    collectionWrapper.getCollection().getDelta().getExternalAction().setId(null);
                }
            }
        }

        if(collectionWrapper.getCollection().getReplicas() != null) {
            for (Replica replica : collectionWrapper.getCollection().getReplicas()) {
                Replica savedReplica = replicaService.findReplicaByNumber(collectionWrapper.getCollection().getName(),
                        replica.getNumber());
                if (savedReplica != null) {
                    replica.setId(savedReplica.getId());
                } else {
                    replica.setId(null);
                }
            }
        }

        initConfiguration(collectionWrapper.getSearchConfiguration());

        //init others

        return collectionWrapper;
    }

    /*TODO sphinx.console-261*/
    private ServerWrapper initServerWrapper(ServerWrapper serverWrapper) {
        Server savedServer = serverService.getServer(serverWrapper.getServer().getName());
        if(savedServer != null) {
            serverWrapper.getServer().setId(savedServer.getId());
        } else {
            serverWrapper.getServer().setId(null);
        }

        for (AdminProcess adminProcess : serverWrapper.getAdminProcesses()) {
            AdminProcess savedAdminProcess =
                    adminProcessService.getAdminProcess(adminProcess.getType(), adminProcess.getServer().getName());
            if(savedAdminProcess != null) {
                adminProcess.setId(savedAdminProcess.getId());
            } else {
                adminProcess.setId(null);
            }
        }

        return serverWrapper;
    }

    @Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.ADD_COLLECTION, method = RequestMethod.POST)
	public @ResponseBody Status addCollection(@RequestBody @Valid CollectionWrapper collectionWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

		logger.info("ABOUT TO ADD COLLECTION: " + collectionWrapper);
		logger.info("COLLECTION TYPE: " + collectionWrapper.getCollection().getType());
		
		logger.info("TASKS MAP: " + tasksMapService.getAllTasks());

        initCollectionWrapper(collectionWrapper);

		AddCollectionTask task = new AddCollectionTask();
		collectionWrapper.getCollection().setCollectionType(CollectionRoleType.SIMPLE);
		task.setType(collectionWrapper.getCollection().getType());
        task.setIndexServer(serverService.getServer(collectionWrapper.getIndexServer().getName()));
        task.setSearchServer(serverService.getServer(collectionWrapper.getSearchServer().getName()));
        task.setCollection(collectionWrapper.getCollection());

        Replica replica = new Replica();
        replica.setNumber(replicaService.createReplicaNumber(collectionWrapper.getCollection().getName()));
        replica.setCollection(collectionWrapper.getCollection());
        collectionWrapper.getCollection().getReplicas().add(replica);
        
        Delta delta = collectionWrapper.getCollection().getDelta();
        if(delta != null){
            delta.setCollection(collectionWrapper.getCollection());
            delta.setPeriod(new Date());
        }

        if(collectionWrapper.getCollection().getDelta() != null && collectionWrapper.getCollection().getDelta().getExternalAction() != null) {
            collectionWrapper.getCollection().getDelta().getExternalAction().setDataSource(collectionWrapper.getSearchConfiguration().getDatasource());
        }

        Configuration searchConfiguration = collectionWrapper.getSearchConfiguration();
        
        if (searchConfiguration != null && (searchConfiguration.getName() == null || searchConfiguration.getName().equals(""))) {
        	searchConfiguration.setName(createSearchConfigurationName(collectionWrapper.getCollection().getName(), replica.getNumber()));
            task.setMainSqlQuery(collectionWrapper.getTableName() == null ?
                    searchConfiguration.getMainSqlQuery() : generateSphinxConfService.createSphinxQuery(collectionWrapper.getTableName(), searchConfiguration.getFieldMappings()));
            logger.info("DELTA SQL QUERY: " + searchConfiguration.getDeltaSqlQuery());
            task.setDeltaSqlQuery(searchConfiguration.getDeltaSqlQuery());
        }
        
        Configuration indexConfiguration = collectionWrapper.getIndexConfiguration();
        
        if (indexConfiguration != null && (indexConfiguration.getName() == null || indexConfiguration.getName().equals(""))) {
        	indexConfiguration.setName("index-conf-" + collectionWrapper.getCollection().getName());
        }

        task.setReplicaNumber(replica.getNumber());
        task.setSearchConfigurationPort(collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
        task.setDistributedConfigurationPort(collectionWrapper.getDistributedConfigurationPort().getDistributedConfigurationPort());
        logger.info("DISTRIBUTED PORT: " + collectionWrapper.getDistributedConfigurationPort().getDistributedConfigurationPort());
        task.setSearchConfiguration(searchConfiguration);
        task.setIndexConfiguration(indexConfiguration);
		task.setCollectionName(collectionWrapper.getCollection().getName());
        task.setCronSchedule(collectionWrapper.getCronSchedule().getCronSchedule());
        logger.info("DELTA SQL QUERY: " + searchConfiguration.getDeltaSqlQuery());
        if(searchConfiguration != null && searchConfiguration.getDeltaSqlQuery() != null) {
            task.setMergeDeltaCronSchedule(collectionWrapper.getMainCronSchedule().getCronSchedule());
        }

		return execute(task, true);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_INDEX, method = RequestMethod.POST)
    public @ResponseBody Status makeCollectionFullRebuildIndex(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
                                                               @PathVariable @ExistsInDB(entityClass = Server.class, fieldName = "name") String serverName){

    	logger.info("ABOUT TO EXECUTE FULL REBUILD INDEX: " + collectionName);
        MakeCollectionFullRebuildIndexTask task = new MakeCollectionFullRebuildIndexTask();
        
        Collection collection = collectionService.getCollection(collectionName);
        
        if (collection != null) {
        	task.setType(collection.getType());
        }

        initFullRebuildTask(task, collectionName, serverName);

        return execute(task, true);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_APPLY, method = RequestMethod.POST)
    public @ResponseBody Status makeCollectionFullRebuildApply(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
                                                               @PathVariable @ExistsInDB(entityClass = Server.class, fieldName = "name") String serverName){
         
    	logger.info("ABOUT TO EXECUTE FULL REBUILD APPLY: " + collectionName);
        Status status;
        FullIndexingState fullIndexingState = collectionsInfoService.getFullIndexingResult(collectionName).getFullIndexingState();
        if(fullIndexingState == FullIndexingState.READY_FOR_APPLY || fullIndexingState == FullIndexingState.ERROR_APPLY || fullIndexingState == FullIndexingState.OK){

            MakeCollectionFullRebuildApplyTask task = new MakeCollectionFullRebuildApplyTask();
            
            Collection collection = collectionService.getCollection(collectionName);
            
            if (collection != null) {
            	task.setType(collection.getType());
            }

            initFullRebuildTask(task, collectionName, serverName);

            status = execute(task, false);

        }
        else {
            status = Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.ILLEGAL_FULL_INDEXING_APPLY_ACTION);
        }

        return status;
    }

    private void initFullRebuildTask(MakeCollectionFullRebuildTask task, String collectionName, String serverName){
        task.setCollection(collectionService.getCollection(collectionName));

        Server server = serverService.getServer(serverName);
        task.setIndexServer(server);

        SphinxProcess indexingSphinxProcess = task instanceof MakeCollectionFullRebuildApplyTask ? processService.findFullIndexingProcess(collectionName)
                : processService.findIndexingProcess(collectionName);
        Configuration indexConfiguration = indexingSphinxProcess.getConfiguration();
        task.setIndexConfiguration(indexConfiguration);
        task.setMainSqlQuery(indexConfiguration.getMainSqlQuery());
        task.setDeltaSqlQuery(indexConfiguration.getDeltaSqlQuery());

        SphinxProcess searchSphinxProcess = processService.findSearchProcesses(collectionName).get(0);
        Configuration searchConfiguration = searchSphinxProcess.getConfiguration();
        task.setSearchConfiguration(searchConfiguration);

        Integer configurationPort = getSearchConfigurationPortWrapper(searchConfiguration.getId()).getSearchConfigurationPort();
        task.setSearchConfigurationPort(configurationPort);
        
        Integer distributedConfigurationPort = getDistributedConfigurationPortWrapper(searchConfiguration.getId()).getDistributedConfigurationPort();
        task.setDistributedConfigurationPort(distributedConfigurationPort);

        task.setCronSchedule(scheduledTaskService.findByCollectionName(searchSphinxProcess.getIndexName(), ScheduledTaskType.INDEXING_DELTA).getCronSchedule());
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_COLLECTION, method = RequestMethod.POST)
    public @ResponseBody Status deleteCollection(@PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        logger.info("TASKS MAP: " + tasksMapService.getAllTasks());
        Status status = null;

        DeleteCollectionTask task = new DeleteCollectionTask();
        Collection collection = collectionService.getCollection(collectionName);
        if (collection != null) {
            task.setCollection(collection);
            List<SphinxProcess> searchProcesses = processService.findSearchProcesses(collectionName);
            Configuration searchConfiguration = CollectionUtils.isNotEmpty(searchProcesses) ? searchProcesses.get(0).getConfiguration() : null;
            task.setSearchConfiguration(searchConfiguration);
            SphinxProcess indexProcess = processService.findIndexingProcess(collectionName);
            Configuration indexConfiguration = indexProcess != null ? indexProcess.getConfiguration() : null;
            task.setIndexConfiguration(indexConfiguration);
            
            if (searchConfiguration == null || indexConfiguration == null) {
            	task.setDbPartOnly(true);
            	task.setState(task.getChain().getFirstState());
            	logger.info("DELETE COLLECTIOn FIRST STATE: " + task.getState());
            }

           status = execute(task, false);
        } else {
            logger.error("Fail to find collection with name " + collectionName);
            status = Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE);
        }

        return status;
    }

    @Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.ADD_SERVER, method = RequestMethod.POST)
	public @ResponseBody Status addServer(@RequestBody @Valid Server server, BindingResult result) {
		try {
			serverService.addServer(server);
		} catch (Throwable e) {
			logger.error("ERROR OCCURED WHILE ADDING NEW SERVER: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE);
		}

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_SERVER, method = RequestMethod.DELETE)
    public @ResponseBody Status deleteServer(@PathVariable("id") @ExistsInDB(entityClass = Server.class, fieldName = "id") Long serverId) {
        logger.info("TASKS MAP: " + tasksMapService.getAllTasks());

        List<SphinxProcess> sphinxProcesses = processService.findByServerId(serverId);
        if(sphinxProcesses != null && sphinxProcesses.size() > 0) {
        	StringBuffer sb = new StringBuffer();

            for(SphinxProcess sphinxProcess : sphinxProcesses) {
                DeleteProcessFromServerTask task = new DeleteProcessFromServerTask();
                task.setCollectionName(sphinxProcess.getIndexName());
                task.setSearchServer(serverService.getServer(serverId));
                task.setIndexServer(serverService.getServer(serverId));

                execute(task, serverService.getServer(serverId).getName(), false);

            	sb.append(sphinxProcess.getIndexName() + ",");
            }

            throw new ApplicationException("Невозможно удалить сервер, на сервере работают sphinx процессы для следующих коллекций: " + sb.toString() + " для удаления сервера необходимо удалить коллекции");

        } else {
            try {
                adminProcessService.deleteAdminProcessesByServer(serverId);
                serverService.deleteServer(serverId);
            } catch (Throwable e) {
                logger.error("ERROR OCCURED WHILE DELETING SERVER: ", e);
                return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, e.getMessage());
            }
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_SERVER_BY_NAME, method = RequestMethod.DELETE)
    public @ResponseBody Status deleteServer(@PathVariable("name") @ExistsInDB(entityClass = Server.class, fieldName = "name") String serverName) {
        logger.info("TASKS MAP: " + tasksMapService.getAllTasks());

        List<SphinxProcess> sphinxProcesses = processService.findByServerName(serverName);
        if(sphinxProcesses != null && sphinxProcesses.size() > 0) {
        	StringBuffer sb = new StringBuffer();
        	
            for(SphinxProcess sphinxProcess : sphinxProcesses) {
                DeleteProcessFromServerTask task = new DeleteProcessFromServerTask();
                task.setCollectionName(sphinxProcess.getIndexName());
                task.setSearchServer(serverService.getServer(serverName));
                task.setIndexServer(serverService.getServer(serverName));

                execute(task, serverName, false);
            	
            	sb.append(sphinxProcess.getIndexName() + ",");
            }
            
            throw new ApplicationException("Невозможно удалить сервер, на сервере работают sphinx процессы для следующих коллекций: " +
                    sb.toString() + " для удаления сервера необходимо удалить коллекции");
        } else {
            try {
                adminProcessService.deleteAdminProcessesByServer(serverName);
                serverService.deleteServer(serverName);
            } catch (Throwable e) {
                logger.error("ERROR OCCURED WHILE DELETING SERVER: ", e);

                return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, e.getMessage());
            }
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.ADD_ADMIN_PROCESS, method = RequestMethod.POST)
    public @ResponseBody Status addAdminProcess(@RequestBody @Valid AdminProcess adminProcess, BindingResult result) {
        try {
            logger.debug("ADD ADMIN PROCESS " + adminProcess);
            adminProcess.setServer(serverService.getServer(adminProcess.getServer().getName()));
            adminProcessService.addAdminProcess(adminProcess);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE ADDING NEW ADMIN PROCESS: ", e);
            String message = ExceptionUtils.getRootCauseMessage(e);
            if (message != null && message.contains("Duplicate")) {
            	message = "Процесс Координатора уже сконфигурирован";
            }

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, message);
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.ADD_ADMIN_PROCESSES, method = RequestMethod.POST)
    public @ResponseBody Status addAdminProcesses(@RequestBody @Valid ServerWrapper serverWrapper, BindingResult result) {
        initServerWrapper(serverWrapper);

        List<AdminProcess> dirtyAdminProcesses = serverWrapper.getAdminProcesses();
        List<AdminProcess> adminProcesses = new LinkedList<AdminProcess>();
        for(AdminProcess adminProcess : dirtyAdminProcesses) {
            if(adminProcess != null) {
                adminProcesses.add(adminProcess);
            }
        }
        Server server = serverWrapper.getServer();
        try {
            logger.debug("ADD SERVER " + server + " WITH ADMIN PROCESSES " + Arrays.deepToString(adminProcesses.toArray()));
            adminProcessService.addAdminProcesses(adminProcesses, server);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE ADDING NEW SERVER: + " + server + "  WITH ADMIN PROCESSES: " + Arrays.deepToString(adminProcesses.toArray()), e);
            String message = ExceptionUtils.getRootCauseMessage(e);
            if (message != null && message.contains("Duplicate")) {
                message = "Процесс Координатора уже сконфигурирован";
            }

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, message);
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.UPDATE_ADMIN_PROCESS, method = RequestMethod.PUT)
    public @ResponseBody Status updateAdminProcess(@RequestBody @Valid AdminProcess adminProcess, BindingResult result) {
        try {
            adminProcessService.updateAdminProcess(adminProcess);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE UPDATING ADMIN PROCESS: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, ExceptionUtils.getRootCauseMessage(e));
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_ADMIN_PROCESS, method = RequestMethod.DELETE)
    public @ResponseBody Status deleteAdminProcess(@PathVariable("id") @ExistsInDB(entityClass = AdminProcess.class, fieldName = "id") Long adminProcessId) {
        try {
            adminProcessService.deleteAdminProcess(adminProcessId);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE DELETING ADMIN PROCESS: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE);
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.REBUILD_COLLECTION, method = RequestMethod.POST)
	public @ResponseBody Status rebuildCollection(@PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
		logger.info("ABOUT TO FIRE REBUILD COLLECTION TASK, FOR: " + collectionName);
		
		MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

		if (!monitoringService.canExecuteIndexing(collectionName, FIRST_REPLICA)) {
			logger.info("INDEXING HAS NOT BEEN EXECUTED...");
			return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.INDEXING_CANNOT_BE_EXECUTED);	
		}
		
		RebuildCollectionTask task = new RebuildCollectionTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
    	
    	return execute(task, false);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, method = RequestMethod.POST)
    public @ResponseBody Status mergeCollection(@PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        logger.info("ABOUT TO FIRE MERGE COLLECTION TASK, FOR: " + collectionName);
        
        MergeCollectionTask task = new MergeCollectionTask();
    	task.setCollectionName(collectionName);

    	return execute(task, false);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_MERGING, method = RequestMethod.POST)
    public @ResponseBody Status stopMerging(@PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        logger.info("ABOUT TO FIRE STOP MERGE COLLECTION TASK, FOR: " + collectionName);
        
        //функционал остановки такой же как и для stopIndexing - процесс один
        StopIndexingTask task = new StopIndexingTask();
    	task.setCollectionName(collectionName);

    	return execute(task, false);
    }
    
    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_FULL_INDEX_DATA, method = RequestMethod.POST)
    public @ResponseBody Status deleteFullIndexData(@PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        logger.info("ABOUT TO FIRE DELETE INDEX DATA TASK, FOR: " + collectionName);
        
        DeleteFullIndexDataTask task = new DeleteFullIndexDataTask();
    	task.setCollectionName(collectionName);

    	return execute(task, false);
    }

	@Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MODIFY_COLLECTION_ATTRIBUTES, method = RequestMethod.POST)
    public @ResponseBody Status modifyCollectionAttributes(@RequestBody @Valid CollectionWrapper collectionWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        logger.info("ABOUT TO MODIFY COLLECTION ATTRIBUTES...");
        logger.info("TASKS MAP: " + tasksMapService.getAllTasks());
        logger.info("COLLETION TYPE: " + collectionWrapper.getCollection().getType());

        initCollectionWrapper(collectionWrapper);

        Configuration searchConfiguration = collectionWrapper.getSearchConfiguration();
        // Возвращаем в филды конфигурацию, убранную при преобразование JSON из-за зацикливания
        searchConfiguration.setSourceConfigurationFieldsConfiguration();
        searchConfiguration.setSearchConfigurationFieldsConfiguration();

        if (searchConfiguration != null && (searchConfiguration.getName() == null || searchConfiguration.getName().equals(""))) {
        	searchConfiguration.setName("search-conf-" + collectionWrapper.getCollection().getName());
        }
        
        Configuration indexConfiguration = collectionWrapper.getIndexConfiguration();
        // Возвращаем в филды конфигурацию, убранную при преобразование JSON из-за зацикливания
        indexConfiguration.setSourceConfigurationFieldsConfiguration();
        indexConfiguration.setSearchConfigurationFieldsConfiguration();

        if (indexConfiguration != null && (indexConfiguration.getName() == null || indexConfiguration.getName().equals(""))) {
        	indexConfiguration.setName("index-conf-" + collectionWrapper.getCollection().getName());
        }
        
        
        ModifyCollectionAttributesTask task;

        Collection collection = collectionService.getCollection(collectionWrapper.getCollection().getName()); //mb is different on server and client sides
        Server indexServer = serverService.getServer(collectionWrapper.getIndexServer().getName());

        if(collection.getIsProcessingFailed()) {
            logger.info("ABOUT TO CREATE ModifyCollectionAttributesRestoreFailureTask...");
            task = new ModifyCollectionAttributesRestoreFailureTask();
        } else {
            if(checkIfMappingChanged(indexConfiguration) ||
                    checkIfMappingChanged(searchConfiguration)) {
                logger.info("ABOUT TO CREATE ModifyCollectionAttributesTask...");
                task = new MakeCollectionFullRebuildIndexTask();
                indexServer = serverService.getServer(collectionWrapper.getFullIndexingServer().getName());
            } else {
                logger.info("ABOUT TO CREATE ModifyCollectionAttributesNoChangeTask...");
                task = new ModifyCollectionAttributesNoChangeTask();
            }
        }
        
        Delta delta = collectionWrapper.getCollection().getDelta();
        if(delta != null){
            DeleteScheme deleteScheme = delta.getDeleteScheme();
            if (deleteScheme != null) {
            	if (collection.getDelta() != null && collection.getDelta().getDeleteScheme() != null) {
            	    deleteScheme.setId(collection.getDelta().getDeleteScheme().getId());
            	}
            }
        }

        task.setType(collectionWrapper.getCollection().getType());
        task.setCollection(collectionWrapper.getCollection());
        task.setIndexConfiguration      (indexConfiguration);
        task.setSearchConfigurationPort(collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
        if (collectionWrapper.getDistributedConfigurationPort() != null) {
            task.setDistributedConfigurationPort(collectionWrapper.getDistributedConfigurationPort().getDistributedConfigurationPort());
        }
        task.setSearchConfiguration(searchConfiguration);
        task.setCronSchedule(collectionWrapper.getCronSchedule().getCronSchedule());
        task.setSearchServer(serverService.getServer(collectionWrapper.getSearchServer().getName()));
        task.setIndexServer(indexServer);
        if(collectionWrapper.getSearchConfiguration() != null && collectionWrapper.getSearchConfiguration().getDeltaSqlQuery() != null) {
            task.setMergeDeltaCronSchedule(collectionWrapper.getMainCronSchedule().getCronSchedule());
        }
        task.setMainSqlQuery(collectionWrapper.getTableName() == null ?
                searchConfiguration.getMainSqlQuery() : generateSphinxConfService.createSphinxQuery(collectionWrapper.getTableName(), searchConfiguration.getFieldMappings()));
        task.setDeltaSqlQuery(searchConfiguration.getDeltaSqlQuery());

        return execute(task, false);
	}

    private String createSearchConfigurationName(String collectionName, Long replicaNumber) {
        return "search-conf-" + collectionName + "-" + replicaNumber;
    }

    private Configuration getOldConfigurationByNew(Configuration newConfiguration) {
        return configurationService.getConfiguration(newConfiguration.getName());
    }

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.CHANGE_COLLECTION_UPDATE_SCHEDULE, method = RequestMethod.POST)
	public @ResponseBody Status changeCollectionUpdateSchedule(@RequestBody @Valid UpdateScheduleWrapper updateScheduleWrapper, BindingResult result) {
		logger.info("ABOUT TO CHANGE COLLECTION UPDATE SCHEDULE, FOR: " + updateScheduleWrapper.getCollectionName() + ", WITH CRON: " + updateScheduleWrapper.getCronExpression());
		logger.info("SCHEDULE TYPE: " + updateScheduleWrapper.getType());

        RequestWrapper requestWrapper = new RequestWrapper();
        requestWrapper.setParameter(updateScheduleWrapper.getCronExpression());
        Boolean validCron = (Boolean)validateCronExpression(requestWrapper).getResult();
        if(validCron){
            ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(updateScheduleWrapper.getCollectionName(), updateScheduleWrapper.getType());

            if (updateScheduleWrapper.getType() == ScheduledTaskType.INDEXING_DELTA) {
                rebuildCollectionTaskScheduler.reSchedule(scheduledTask, updateScheduleWrapper.getCronExpression());
            } else if (updateScheduleWrapper.getType() == ScheduledTaskType.MERGE_DELTA) {
                mergeCollectionTaskScheduler.reSchedule(scheduledTask, updateScheduleWrapper.getCronExpression());
            } else if (updateScheduleWrapper.getType() == ScheduledTaskType.BUILD_SNIPPET) {
                rebuildSnippetTaskScheduler.reSchedule(scheduledTask, updateScheduleWrapper.getCronExpression());
            }

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
        }
        else {
            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.DATA_INTEGRITY_FAILURE, "Invalid cron expression");
        }

	}

	@Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MOVE_PROCESS_TO_SERVER, method = RequestMethod.POST)
    public @ResponseBody Status moveProcessToServer(@RequestBody @Valid MoveProcessToServerWrapper moveProcessToServerWrapper, BindingResult result) {
        SphinxProcess searchSphinxProcess = moveProcessToServerWrapper.getSearchSphinxProcess();
        Server oldServer = serverService.getServer(searchSphinxProcess.getServer().getName());
        SphinxProcess indexSphinxProcess =
                processService.findByCollectionNameAndType(oldServer.getName(), searchSphinxProcess.getIndexName(), SphinxProcessType.INDEXING).get(0); //TODO

        indexSphinxProcess.setServer(serverService.getServer(moveProcessToServerWrapper.getNewServer().getName()));
        searchSphinxProcess.setServer(serverService.getServer(moveProcessToServerWrapper.getNewServer().getName()));

        MoveProcessToServerTask task = new MoveProcessToServerTask();
        task.setCollectionName      (searchSphinxProcess.getIndexName());
        task.setSearchConfiguration (searchSphinxProcess.getConfiguration());
        task.setIndexConfiguration  (indexSphinxProcess.getConfiguration());
        task.setSearchServer        (serverService.getServer(moveProcessToServerWrapper.getNewServer().getName()));
        task.setIndexServer         (serverService.getServer(moveProcessToServerWrapper.getNewServer().getName()));
        task.setCronSchedule        (scheduledTaskService.findByCollectionName(searchSphinxProcess.getIndexName(), ScheduledTaskType.INDEXING_DELTA).getCronSchedule());
        task.setOldServerName       (oldServer.getName());

        execute(task, oldServer.getName(), false);

        MoveProcessToServerDeleteTask deleteTask = new MoveProcessToServerDeleteTask();
        deleteTask.setSearchServer(oldServer);
        deleteTask.setIndexServer(oldServer);
        deleteTask.setCollectionName(searchSphinxProcess.getIndexName());

        return execute(task, false);
    }

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_ALL_PROCESSES, method = RequestMethod.POST)
	public @ResponseBody Status stopAllProcesses(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
				
		StopAllProcessesTask task = new StopAllProcessesTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
    	
    	return execute(task, false);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_PROCESS, method = RequestMethod.POST)
    public @ResponseBody Status stopProcess(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
                                            @PathVariable @ExistsInDB(entityClass = Replica.class, fieldName = "number") Long replicaNumber) {
        SphinxProcess sphinxProcess = processService.findSearchProcess(collectionName, replicaNumber);

        StopProcessTask task = new StopProcessTask();
        task.setCollectionName(sphinxProcess.getCollection().getName());
        task.setReplicaNumber(replicaNumber);

        return execute(task, false);
    }

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.START_ALL_PROCESSES, method = RequestMethod.POST)
	public @ResponseBody Status startAllProcesses(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
		StartAllProcessesTask task = new StartAllProcessesTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
    	
    	return execute(task, false);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.START_PROCESS, method = RequestMethod.POST)
    public @ResponseBody Status startProcess(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
                                             @PathVariable @ExistsInDB(entityClass = Replica.class, fieldName = "number") Long replicaNumber) {
        SphinxProcess sphinxProcess = processService.findSearchProcess(collectionName, replicaNumber); //TODO

        StartProcessTask task = new StartProcessTask();

        ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(sphinxProcess.getCollection().getName(), ScheduledTaskType.INDEXING_DELTA);

        if (scheduledTask != null) {
            task.setCronSchedule(scheduledTask.getCronSchedule());
        }

        task.setCollectionName(sphinxProcess.getCollection().getName());
        task.setReplicaNumber(replicaNumber);

        return execute(task, false);
    }

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_TASK, method = RequestMethod.POST)
	public @ResponseBody Status stopTask(@RequestBody @Valid TaskWrapper task, BindingResult result) {
		logger.info("ABOUT TO STOP TASK, UID: " + task.getTaskUID());
		
		Task targetTask = tasksMapService.getTask(task.getTaskUID());
        ActivityLog activityLog = activityLogService.getLast(task.getTaskUID());
		
		logger.info("RETRIVED ACTIVITY LOG: " + activityLog);
		
		if (activityLog != null) {
			activityLog.setTaskStatus(TaskStatus.STOPPED);
			activityLogService.save(activityLog);
		}
		
		if (targetTask != null) {
			targetTask.setTaskStatus(TaskStatus.STOPPED);	
		} else {
			if (activityLog == null) {
			    throw new ApplicationException("task not found exception, UID: " + task.getTaskUID());
			}
		}
		
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.PAUSE_TASK, method = RequestMethod.POST)
	public @ResponseBody Status pauseTask(@RequestBody @Valid TaskWrapper task, BindingResult result) {
        logger.info("ABOUT TO PAUSE TASK, UID: " + task.getTaskUID());
		
		Task targetTask = tasksMapService.getTask(task.getTaskUID());
		ActivityLog activityLog = activityLogService.getLast(task.getTaskUID());
		
		logger.info("RETRIVED ACTIVITY LOG: " + activityLog);
		
		if (activityLog != null) {
			activityLog.setTaskStatus(TaskStatus.PAUSED);
			activityLogService.save(activityLog);
		}
		
		if (targetTask != null) {
			targetTask.setTaskStatus(TaskStatus.PAUSED);	
		} else {	
			if (activityLog == null) {
			    throw new ApplicationException("task not found exception, UID: " + task.getTaskUID());
			}
		}
		
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.RESUME_TASK, method = RequestMethod.POST)
	public @ResponseBody Status resumeTask(@RequestBody @Valid TaskWrapper task, BindingResult result) {
        logger.info("ABOUT TO RESUME TASK, UID: " + task.getTaskUID());
		
		Task targetTask = tasksMapService.getTask(task.getTaskUID());
		
		if (targetTask != null && targetTask.getTaskStatus() == TaskStatus.PAUSED) {
			targetTask.setTaskStatus(TaskStatus.RUNNING);
			getTaskService(targetTask.getTaskName()).execute(targetTask);
		} else {
			throw new ApplicationException("task is not in PAUSED status or server has been restarted and tasksMap does not contain this task, UID: " + task.getTaskUID());
		}
		
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE, task.getTaskUID());
	}

	/**
	  {
		  "type": "MYSQL",
		  "host": "192.168.211.111",
		  "port": "3306",
		  "odbcDsn": "sphinx.console",
		  "user": "root",
		  "password": "root"
      }
	 */
	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.CHECK_DB_CONNECTION, method = RequestMethod.POST)
	public @ResponseBody Status checkDBConnection(@RequestBody @Valid DataSource datasource, BindingResult result) {
		logger.info("ABOUT TO CHECK CONNECTION FOR DATABASE TYPE: " + datasource.getType());
		
		
		org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("checkDataSource");

		dataSource.setDriverClassName(datasource.getType().getDriverClass());
		dataSource.setUrl(datasource.getType().getUrl(datasource));
		
		try {
			dataSource.getConnection(datasource.getUser(), datasource.getPassword());
		} catch (SQLException e) {
			logger.error("could not connect to DB: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.DB_CONNECTION_FAILURE, e.getMessage());
		}
		
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
	}

	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_INDEXING, method = RequestMethod.POST)
	public @ResponseBody Status stopIndexing(@PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
		logger.info("ABOUT TO EXECUTE STOP INDEXING TASK, FOR: " + collectionName);
		
		StopIndexingTask task = new StopIndexingTask();
    	task.setCollectionName(collectionName);

    	return execute(task, false);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_FULL_REBUILD_INDEXING, method = RequestMethod.POST)
    public @ResponseBody Status stopFullRebuildIndexing(
            @PathVariable("collectionName") @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
            @PathVariable @ExistsInDB(entityClass = Server.class, fieldName = "name") String serverName) {
        logger.info("ABOUT TO EXECUTE STOP FULL INDEXING TASK, FOR: " + collectionName);

        StopFullIndexingTask task = new StopFullIndexingTask();
        task.setCollectionName(collectionName);

        return execute(task, false);
    }

    @Override
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.VALIDATE_CRON_EXPRESSION, method = RequestMethod.POST)
    public @ResponseBody ResponseWrapper<Boolean> validateCronExpression(@RequestBody RequestWrapper<String> request) {
        return new ResponseWrapper<Boolean>(CronExpression.isValidExpression(request.getParameter()));
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.ADD_CONFIGURATION_TEMPLATE, method = RequestMethod.POST)
    public @ResponseBody Status addConfigurationTemplate(@RequestBody @Valid ConfigurationTemplate configurationTemplate, BindingResult bindingResult) {
        logger.info("ABOUT TO ADD CONFIGURATION TEMPLATE: " + (configurationTemplate != null?configurationTemplate.getName():null));
    	if(configurationTemplate!=null&&configurationTemplate.getConfigurationFields()!=null){
            for(ConfigurationFields configurationFields : configurationTemplate.getConfigurationFields()){
                configurationFields.setConfigurationTemplate(configurationTemplate);
            }
        }
        try {
            configurationTemplateService.addConfigurationTemplate(configurationTemplate);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE ADDING NEW CONFIGURATION TEMPLATE: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE);
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.UPDATE_CONFIGURATION_TEMPLATE, method = RequestMethod.POST)
    public @ResponseBody Status updateConfigurationTemplate(@PathVariable @ExistsInDB(entityClass = ConfigurationTemplate.class, fieldName = "id") Long id,
                                                            @RequestBody @Valid ConfigurationTemplate configurationTemplate, BindingResult result) {
        logger.info("ABOUT TO UPDATE CONFIGURATION TEMPLATE, ID: " + (configurationTemplate != null?configurationTemplate.getId():null));
    	ConfigurationTemplate template = configurationTemplateService.getConfigurationTemplate(id);
        template.setName(configurationTemplate.getName());
        template.setDefaultTemplate(configurationTemplate.getDefaultTemplate());
        template.setDescription(configurationTemplate.getDescription());
        if(configurationTemplate != null && configurationTemplate.getConfigurationFields() != null){
            for(ConfigurationFields configurationFields : configurationTemplate.getConfigurationFields()){
                configurationFields.setId(null);
            }
        }
        if (!template.getConfigurationFields().isEmpty()) {
            configurationService.clearConfigurationFields(id);
            template.getConfigurationFields().clear();
        }
        
        template.setConfigurationFields(configurationTemplate.getConfigurationFields());
        
        try {
            configurationTemplateService.updateConfigurationTemplate(template);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE UPDATING CONFIGURATION TEMPLATE: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, e.getMessage());
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_CONFIGURATION_TEMPLATE, method = RequestMethod.DELETE)
    public @ResponseBody Status deleteConfigurationTemplate(@PathVariable @ExistsInDB(entityClass = ConfigurationTemplate.class, fieldName = "id") Long id) {
        try {
                configurationTemplateService.deleteConfigurationTemplate(id);
                configurationService.clearConfigurationFields(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) { //org.hibernate.exception.ConstraintViolationException
            String message = null;
            if(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                List<Configuration> configurations = configurationService.getConfigurationByConfigurationTemplate(id);
                String configurationString = configurations.get(0).getName();
                for(int i = 1; i < configurations.size(); i++) {
                    configurationString += ", " + configurations.get(i).getName();
                }
                message = MessageFormat.format("Невозможно удалить шаблон он задействован в конфигурациях: {0}", configurationString);
            }
            logger.error("ERROR OCCURED WHILE DELETING CONFIGURATION TEMPLATE: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, message);
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE DELETING CONFIGURATION TEMPLATE: ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, e.getMessage());
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DISABLE_SCHEDULING, method = RequestMethod.POST)
    public @ResponseBody Status disableScheduling(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
                                                  @PathVariable String taskType) {
        try {
            ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(collectionName, ScheduledTaskType.valueOf(taskType));
            if (ScheduledTaskType.valueOf(taskType) == ScheduledTaskType.INDEXING_DELTA) {
                rebuildCollectionTaskScheduler.disableScheduling(scheduledTask);
            } else if (ScheduledTaskType.valueOf(taskType) == ScheduledTaskType.MERGE_DELTA) {
            	mergeCollectionTaskScheduler.disableScheduling(scheduledTask);
            } else {
            	 rebuildSnippetTaskScheduler.disableScheduling(scheduledTask);
            }

        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE DISABLING SCHEDULING FOR COLLECTION "+ collectionName + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, e.getMessage());
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.ENABLE_SCHEDULING, method = RequestMethod.POST)
    public @ResponseBody Status enableScheduling(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName,
                                                 @PathVariable String taskType) {
        try {
            ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(collectionName, ScheduledTaskType.valueOf(taskType));
            if (ScheduledTaskType.valueOf(taskType) == ScheduledTaskType.INDEXING_DELTA) {
                rebuildCollectionTaskScheduler.enableScheduling(scheduledTask);
            } else if (ScheduledTaskType.valueOf(taskType) == ScheduledTaskType.MERGE_DELTA)  {
            	mergeCollectionTaskScheduler.enableScheduling(scheduledTask);
            } else {
            	rebuildSnippetTaskScheduler.enableScheduling(scheduledTask);
            }
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE ENABLING SCHEDULING FOR COLLECTION "+ collectionName + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, e.getMessage());
        }

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.CREATE_REPLICA, method = RequestMethod.POST)
    public @ResponseBody Status createReplica(@RequestBody @Valid ReplicaWrapper replicaWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(replicaWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        String collectionName = replicaWrapper.getCollectionName();
        Long replicaNumber = null;
        CreateReplicaTask task = new CreateReplicaTask();
        try {
            Collection collection = collectionService.getCollection(collectionName);
            replicaNumber = replicaService.createReplicaNumber(collectionName);
            task.setSearchConfigurationPort(replicaWrapper.getSearchPort());
            task.setDistributedConfigurationPort(replicaWrapper.getDistributedPort());
            task.setCollectionName(collectionName);
            task.setReplicaNumber(replicaNumber);
            task.setType(collection.getType());
            SphinxProcess indexingSphinxProcess = processService.findIndexingProcess(collectionName);
            task.setIndexServer(indexingSphinxProcess.getServer());
            
            if (replicaWrapper.getServer() != null) {
                logger.info("SEARCH SERVER: " + replicaWrapper.getServer().getName());
            }
            
            task.setSearchServer(replicaWrapper.getServer());
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, FIRST_REPLICA);
            Configuration configuration = searchSphinxProcess.getConfiguration();
            
            if (configuration != null) {
            	task.setMainSqlQuery(configuration.getMainSqlQuery());
//            	Collection collection = collectionService.getCollection(collectionName);
            	if (collection != null && collection.getType() == CollectionType.MAIN_DELTA) {
            		task.setDeltaSqlQuery(configuration.getDeltaSqlQuery());
            	}
            }
            
            Configuration newSearchConfiguration = deepCopyService.deepCopyConfiguration(configuration);
            newSearchConfiguration.setName(createSearchConfigurationName(collectionName, task.getReplicaNumber()));
            task.setSearchConfiguration(newSearchConfiguration);

            return execute(task, true);
        } catch (Throwable e) {
            logger.error("ERROR OCCURRED WHILE CREATING FOR COLLECTION "+ collectionName + " REPLICA NUMBER "+ replicaNumber + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(), e);
        }
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.REMOVE_REPLICA, method = RequestMethod.POST)
    public @ResponseBody Status removeReplica(@RequestBody @Valid ReplicaWrapper replicaWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(replicaWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        logger.info("ABOUT TO REMOVE REPLICA OF COLLECTION: " + replicaWrapper.getCollectionName() + ", REPLICA NUMBER: " + replicaWrapper.getReplicaNumber());
    	Collection collection = collectionService.getCollection(replicaWrapper.getCollectionName());
        logger.info("COLLECTION TYPE: " + collection.getCollectionType() + ", REPLICA NUMBER: " + replicaWrapper.getReplicaNumber());
        RemoveReplicaTask task = new RemoveReplicaTask();
        if (collection.getCollectionType() == CollectionRoleType.SIMPLE) {
        	if (replicaWrapper.getReplicaNumber() == FIRST_REPLICA) {
        		logger.info("CANNOT DELETE FIRST REPLICA OF SIMPLE COLLECTION");

                return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(),
                        "Невозможно удалить первую реплику простой коллекции. Первая реплика удаляется вместе с коллекцией");
        	}
        }
    	
    	Replica replica = replicaService.findReplicaByNumber(replicaWrapper.getCollectionName(), replicaWrapper.getReplicaNumber());
       
        
        Long count = replicaService.countReplicas(replicaWrapper.getCollectionName());
		logger.info("NUMBER OF REPLICAS: " + count + ", FOR: " + replicaWrapper.getCollectionName());
		
		if (count == SINGLE_REPLICA) {
			logger.info("CANNOT DELETE LAST REPLICA...");

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(), "Невозможно удалить единственную ноду поиска");
		}
        
        try {
            task.setCollectionName(replica.getCollection().getName());
            task.setReplicaNumber(replica.getNumber());

            return execute(task, false);
        } catch (Throwable e) {
            logger.error("ERROR OCCURRED WHILE REMOVING FOR COLLECTION "+ replica.getCollection().getName() + " REPLICA NUMBER "+ replica.getNumber() + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(), e);
        }
    }

    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MODIFY_REPLICA_PORT, method = RequestMethod.POST)
    public @ResponseBody Status modifyReplicaPort(@RequestBody @Valid ReplicaWrapper replicaWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(replicaWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        Replica replica = replicaService.findReplicaByNumber(replicaWrapper.getCollectionName(), replicaWrapper.getReplicaNumber());
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(replicaWrapper.getCollectionName(), replica.getNumber());
        Configuration configuration = searchSphinxProcess.getConfiguration();
        ModifyReplicaTask task = new ModifyReplicaTask();
        try {
            task.setCollectionName(replicaWrapper.getCollectionName());
            task.setReplicaNumber(replica.getNumber());
            task.setNewSearchConfigurationPort(replicaWrapper.getSearchPort());
            task.setNewDistributedConfigurationPort(replicaWrapper.getDistributedPort());
            task.setSearchConfiguration(configuration);
            task.setCronSchedule(scheduledTaskService.findByCollectionName(replicaWrapper.getCollectionName(), ScheduledTaskType.INDEXING_DELTA).getCronSchedule());

            return execute(task, false);
        } catch (Throwable e) {
            logger.error("ERROR OCCURRED WHILE MODIFY REPLICA PORT FOR COLLECTION "+ replica.getCollection().getName() + " REPLICA NUMBER "+ replica.getNumber() + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(), e);
        }
    }

    @Override
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.SAVE_DATA_SOURCE, method = {RequestMethod.POST, RequestMethod.PUT})
    public @ResponseBody Status saveDataSource(@RequestBody DataSource dataSource) {
        dataSourceService.save(dataSource);

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

	@Override
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.ADD_DISTRIBUTED_COLLECTION, method = RequestMethod.POST)
	public @ResponseBody Status addDistributedCollection(@RequestBody DistributedCollectionWrapper distributedCollectionWrapper) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(distributedCollectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        logger.info("ABOUT TO ADD DISTRIBUTED COLLECTION: " + distributedCollectionWrapper);
		logger.info("TASKS MAP: " + tasksMapService.getAllTasks());

        initCollectionWrapper(distributedCollectionWrapper);

		AddDistributedCollectionTask task = new AddDistributedCollectionTask();
		
		convertNodes(task, distributedCollectionWrapper);
		
		distributedCollectionWrapper.getCollection().setCollectionType(CollectionRoleType.DISTRIBUTED);
		distributedCollectionWrapper.getCollection().setType(CollectionType.DISTRIBUTED);
		task.setType(distributedCollectionWrapper.getCollection().getType());
        task.setSearchServer(serverService.getServer(distributedCollectionWrapper.getSearchServer().getName()));
        task.setCollection(distributedCollectionWrapper.getCollection());

        Replica replica = new Replica();
        replica.setNumber(replicaService.createReplicaNumber(distributedCollectionWrapper.getCollection().getName()));
        replica.setCollection(distributedCollectionWrapper.getCollection());
        distributedCollectionWrapper.getCollection().getReplicas().add(replica);

        Configuration searchConfiguration = distributedCollectionWrapper.getSearchConfiguration();
        
        if (searchConfiguration != null && (searchConfiguration.getName() == null || searchConfiguration.getName().equals(""))) {
        	searchConfiguration.setName(createSearchConfigurationName(distributedCollectionWrapper.getCollection().getName(), replica.getNumber()));
        }

        task.setReplicaNumber(replica.getNumber());
        task.setSearchConfigurationPort(distributedCollectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
        task.setSearchConfiguration(searchConfiguration);
        task.setCollectionName(distributedCollectionWrapper.getCollection().getName());

		return execute(task, true);
	}
	
    @Override
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.RELOAD_DISTRIBUTED_COLLECTION, method = RequestMethod.POST)
    public @ResponseBody Status reloadDistributedCollection(@PathVariable("collectionName") String collectionName) {
        logger.info("ABOUT TO RELOAD DISTRIBUTED COLLECTION: " + collectionName);
        logger.info("TASKS MAP: " + tasksMapService.getAllTasks());

        ReloadDistributedCollectionTask task = new ReloadDistributedCollectionTask();
        Collection collection = collectionService.getCollection(collectionName);
       
        task.setCollection(collection);

        logger.info("REPLICA NUMBER: " + task.getReplicaNumber());
        
        Set<DistributedCollectionNode> nodes = collection.getDistributedCollectionNodes();
        
        reformNodes(nodes);
        
       
        task.setNodes(collection.getDistributedCollectionNodes());
        task.setType(CollectionType.DISTRIBUTED);
        task.setCollectionName(collectionName);

        return execute(task, false);
    }

	@Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.CREATE_DISTRIBUTED_REPLICA, method = RequestMethod.POST)
	public @ResponseBody Status createDistributedReplica(@RequestBody @Valid ReplicaWrapper replicaWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(replicaWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        String collectionName = replicaWrapper.getCollectionName();
        Long replicaNumber = null;
        CreateDistributedReplicaTask task = new CreateDistributedReplicaTask();
        try {
            replicaNumber = replicaService.createReplicaNumber(collectionName);
            task.setSearchConfigurationPort(replicaWrapper.getSearchPort());
            task.setCollectionName(collectionName);
            task.setReplicaNumber(replicaNumber);

            if (replicaWrapper.getServer() != null) {
                logger.info("SEARCH SERVER: " + replicaWrapper.getServer().getName());
            }

            Collection distributedCollection = collectionService.getCollection(collectionName);
            task.setCollection(distributedCollection);

            Set<DistributedCollectionNode> nodes = distributedCollection.getDistributedCollectionNodes();

            task.setNodes(nodes);

            task.setSearchServer(replicaWrapper.getServer());
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, FIRST_REPLICA);
            Configuration configuration = searchSphinxProcess.getConfiguration();

            Configuration newSearchConfiguration = deepCopyService.deepCopyConfiguration(configuration);
            newSearchConfiguration.setName(createSearchConfigurationName(collectionName, task.getReplicaNumber()));
            task.setSearchConfiguration(newSearchConfiguration);

            return execute(task, true);
        } catch (Throwable e) {
            logger.error("ERROR OCCURRED WHILE CREATING FOR COLLECTION "+ collectionName + " REPLICA NUMBER "+ replicaNumber + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(), e);
        }
	}

	@Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MODIFY_DISTRIBUTED_REPLICA_PORT, method = RequestMethod.POST)
	public @ResponseBody Status modifyDistributedReplicaPort(@RequestBody @Valid ReplicaWrapper replicaWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(replicaWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

		Replica replica = replicaService.findReplicaByNumber(replicaWrapper.getCollectionName(), replicaWrapper.getReplicaNumber());
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(replicaWrapper.getCollectionName(), replica.getNumber());
        Configuration configuration = searchSphinxProcess.getConfiguration();
        ModifyDistributedReplicaTask task = new ModifyDistributedReplicaTask();
        try {
            task.setCollectionName(replicaWrapper.getCollectionName());
            task.setReplicaNumber(replica.getNumber());
            task.setNewSearchConfigurationPort(replicaWrapper.getSearchPort());
            task.setSearchConfiguration(configuration);
            
            Collection distributedCollection = collectionService.getCollection(replicaWrapper.getCollectionName());
            task.setCollection(distributedCollection);
            
            Set<DistributedCollectionNode> nodes = distributedCollection.getDistributedCollectionNodes();
            
            task.setNodes(nodes);
            
            return execute(task, false);
        } catch (Throwable e) {
            logger.error("ERROR OCCURRED WHILE MODIFY REPLICA PORT FOR COLLECTION "+ replica.getCollection().getName() + " REPLICA NUMBER "+ replica.getNumber() + " : ", e);

            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.FAILURE_LOCAL_DB_CODE, task.getTaskUID(), e);
        }
	}

	@Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES, method = RequestMethod.POST)
	public @ResponseBody Status modifyDistributedCollectionAttributes(@RequestBody @Valid DistributedCollectionWrapper collectionWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        logger.info("ABOUT TO MODIFY DISTRIBUTED COLLECTION ATTRIBUTES...");
	    logger.info("TASKS MAP: " + tasksMapService.getAllTasks());
	    logger.info("COLLETION TYPE: " + collectionWrapper.getCollection().getType());
	    logger.info("CONFIGURATION ID: " + collectionWrapper.getSearchConfiguration().getId());

	    initCollectionWrapper(collectionWrapper);

        Configuration searchConfiguration = collectionWrapper.getSearchConfiguration();

        searchConfiguration.setSearchConfigurationFieldsConfiguration();
        
        if (searchConfiguration != null && (searchConfiguration.getName() == null || searchConfiguration.getName().equals(""))) {
        	searchConfiguration.setName("search-conf-" + collectionWrapper.getCollection().getName());
        }


	    DistributedTask task = null;

	    Collection collection = collectionService.getCollection(collectionWrapper.getCollection().getName());

	    logger.info("COLLECTION: " + collection.getName());

	    if (!collection.getIsProcessingFailed()) {
	    	task = new ModifyDistributedCollectionAttributesTask();
	    } else {
	    	// restore failure
	    	task = new ModifyDistributedCollectionAttributesRestoreFailureTask();
	    }

	    convertNodes(task, collectionWrapper);

	    task.setSearchConfigurationPort(collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
	    task.setType(collectionWrapper.getCollection().getType());
        task.setCollection(collectionWrapper.getCollection());
        task.setSearchConfigurationPort(collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());

        task.setSearchConfiguration(searchConfiguration);
        task.setSearchServer(serverService.getServer(collectionWrapper.getSearchServer().getName()));

	    return execute(task, false);
	}

	@Override
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.CREATE_SNIPPET_CONFIGURATION, method = RequestMethod.POST)
	public @ResponseBody Status createSnippetConfiguration(@RequestBody SnippetConfigurationWrapper snippetConfigurationWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(snippetConfigurationWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

		logger.info("ABOUT TO CREATE SNIPPET, FOR COLLECTION: " + snippetConfigurationWrapper.getCollectionName());

		Collection collection = collectionService.getCollection(snippetConfigurationWrapper.getCollectionName());
		
		if (collection.getSnippetConfiguration() != null) {
			throw new ApplicationException("snippet already exists, for collection: " + snippetConfigurationWrapper.getCollectionName());
		}
		
		CreateSnippetConfigurationTask task = new CreateSnippetConfigurationTask();
		
		task.setCollection(collection);
		task.setCollectionName(collection.getName());
		
		SnippetConfiguration source = snippetConfigurationWrapper.getSnippetConfiguration();
		
		SnippetConfiguration target = new SnippetConfiguration();
		
		reformCreateSnippet(target, source, collection, snippetConfigurationWrapper.getCron());
		
		task.setSnippetConfiguration(target);
		
		return execute(task, false);
	}
	
	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_SNIPPET_CONFIGURATION, method = RequestMethod.GET)
	public @ResponseBody Status deleteSnippetConfiguration(@PathVariable("collectionName") String collectionName) {
		logger.info("ABOUT TO DELETE SNIPPET CONFIGURATION , FOR: " + collectionName);
		
		DeleteSnippetConfigurationTask task = new DeleteSnippetConfigurationTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
    	
    	return execute(task, false);
	}
	
	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.REBUILD_SNIPPETS, method = RequestMethod.GET)
	public @ResponseBody Status rebuildSnippets(@PathVariable("collectionName") String collectionName) {
		logger.info("ABOUT TO REBUILD SNIPPETS, FOR: " + collectionName);
		
		Collection collection = collectionService.getCollection(collectionName);
		
		MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

		if (monitoringService.isCurrentlyRebuildSnippet(collectionName)) {
			logger.info("REBUILD SNIPPET HAS NOT BEEN EXECUTED...");
			return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.REBUILD_SNIPPET_CANNOT_BE_EXECUTED);	
		}
		
		RebuildSnippetsTask task = new RebuildSnippetsTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
    	task.setSnippetConfiguration(collection.getSnippetConfiguration());
    	
    	return execute(task, false);
	}
	
	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_REBUILD_SNIPPETS, method = RequestMethod.GET)
	public @ResponseBody Status stopRebuildSnippets(@PathVariable("collectionName") String collectionName) {
		logger.info("ABOUT TO STOP REBUILD SNIPPETS, FOR: " + collectionName);
		
		Collection collection = collectionService.getCollection(collectionName);
		
		MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
		
		Task snippetsTask = monitoringService.getCurrentlyRebuildSnippetTask(collectionName);

		if (snippetsTask != null) {
			logger.info("REBUILD SNIPPET TASK UID: " + snippetsTask.getTaskUID());
			StopRebuildSnippetsTask task = new StopRebuildSnippetsTask();
	    	task.setCollectionName(collectionName);
	    	task.setReplicaNumber(FIRST_REPLICA);
	        task.setSnippetTaskUID(snippetsTask.getTaskUID());
	        
	        snippetsTask.setTaskStatus(TaskStatus.STOPPED);
	        
	    	return execute(task, false);
		}
		
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);	
	}
	
	@Override
    @ExecuteOnlyWithValidParams
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.STOP_FULL_REBUILD_SNIPPETS, method = RequestMethod.GET)
	public @ResponseBody Status stopFullRebuildSnippets(@PathVariable("collectionName") String collectionName) {
		logger.info("ABOUT TO STOP FULL REBUILD SNIPPETS, FOR: " + collectionName);
		
		Collection collection = collectionService.getCollection(collectionName);
		
		MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
		
		Task snippetsTask = monitoringService.getCurrentlyFullRebuildSnippetTask(collectionName);

		if (snippetsTask != null) {
			
			StopRebuildSnippetsTask task = new StopRebuildSnippetsTask();
	    	task.setCollectionName(collectionName);
	    	task.setReplicaNumber(FIRST_REPLICA);
	        task.setSnippetTaskUID(snippetsTask.getTaskUID());	
	        task.setSnippetFullRebuild(true);
	        snippetsTask.setTaskStatus(TaskStatus.STOPPED);
	    	return execute(task, false);
		}
		
		return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);	
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.MAKE_SNIPPETS_FULL_REBUILD, method = RequestMethod.GET)
    public @ResponseBody Status makeSnippetsFullRebuild(@PathVariable("collectionName") String collectionName) {
        logger.info("ABOUT TO REBUILD SNIPPETS, FOR: " + collectionName);

        Collection collection = collectionService.getCollection(collectionName);

        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        if (monitoringService.isCurrentlyFullRebuildSnippet(collectionName)) {
            logger.info("FULL REBUILD SNIPPET HAS NOT BEEN EXECUTED...");
            return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.REBUILD_SNIPPET_CANNOT_BE_EXECUTED);
        }

        FullRebuildSnippetTask task = new FullRebuildSnippetTask();
        task.setCollectionName(collectionName);
        task.setReplicaNumber(FIRST_REPLICA);
        task.setSnippetConfiguration(collection.getSnippetConfiguration());

        return execute(task, false);
    }
	
	@Override
	@RequestMapping(value = CoordinatorConfigurationRestURIConstants.EDIT_SNIPPET_CONFIGURATION, method = RequestMethod.POST)
	public @ResponseBody Status editSnippetConfiguration(@RequestBody SnippetConfigurationWrapper snippetConfigurationWrapper, BindingResult result) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(snippetConfigurationWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        logger.info("ABOUT TO EDIT SNIPPET, FOR COLLECTION: " + snippetConfigurationWrapper.getCollectionName());

        initSnippetWrapper(snippetConfigurationWrapper);

		Collection collection = collectionService.getCollection(snippetConfigurationWrapper.getCollectionName());
		
		EditSnippetConfigurationTask task = new EditSnippetConfigurationTask();
		
		task.setCollection(collection);
		task.setCollectionName(collection.getName());
		
		SnippetConfiguration source = snippetConfigurationWrapper.getSnippetConfiguration();
		
		SnippetConfiguration target = snippetConfigurationService.findById(source.getId());
		
		reformEditSnippet(target, source, collection, snippetConfigurationWrapper.getCron());
		
		task.setSnippetConfiguration(target);
		
		return execute(task, false);
	}
	
	private void reformCreateSnippet(SnippetConfiguration target, SnippetConfiguration source, Collection collection, CronScheduleWrapper cron) {
		target.setCollection(collection);
		
		ScheduledTask scheduledTask = new ScheduledTask();
		scheduledTask.setCollection(collection);
		scheduledTask.setType(ScheduledTaskType.BUILD_SNIPPET);
		scheduledTask.setIsEnabled(true);
		scheduledTask.setCronSchedule(cron.getCronSchedule());
		
		target.setScheduledTask(scheduledTask);
		    
		Configuration configuration = configurationService.getConfiguration("index-conf-" + collection.getName());
		    
		target.setDataSource(configuration.getDatasource());
		    
		target.setFullMainQuery(source.getFullMainQuery());
		target.setFullPostQuery(source.getFullPostQuery());
		target.setFullPreQuery(source.getFullPreQuery());
		    
		target.setMainQuery(source.getMainQuery());
		target.setPostQuery(source.getPostQuery());
		target.setPreQuery(source.getPreQuery());
		    
		Set<SnippetConfigurationField> fields = new HashSet<SnippetConfigurationField>();
		    
		for (SnippetConfigurationField sourceField : source.getFields()) {
		    SnippetConfigurationField targetField = new SnippetConfigurationField();
		    targetField.setFieldName(sourceField.getFieldName());
		    targetField.setSnippetConfiguration(target);
		    	
		    fields.add(targetField);
		}
		    
		target.setFields(fields);
	}
	
	private void reformEditSnippet(SnippetConfiguration target, SnippetConfiguration source, Collection collection, CronScheduleWrapper cron) {
		ScheduledTask scheduledTask = target.getScheduledTask();
		scheduledTask.setIsEnabled(true);
		scheduledTask.setCronSchedule(cron.getCronSchedule());
		
		    
		Configuration configuration = configurationService.getConfiguration("index-conf-" + collection.getName());
		    
		target.setDataSource(configuration.getDatasource());
		    
		target.setFullMainQuery(source.getFullMainQuery());
		target.setFullPostQuery(source.getFullPostQuery());
		target.setFullPreQuery(source.getFullPreQuery());
		    
		target.setMainQuery(source.getMainQuery());
		target.setPostQuery(source.getPostQuery());
		target.setPreQuery(source.getPreQuery());
		    
		Set<SnippetConfigurationField> fields = new HashSet<SnippetConfigurationField>();
		    
		for (SnippetConfigurationField sourceField : source.getFields()) {
		    SnippetConfigurationField targetField = new SnippetConfigurationField();
		    targetField.setFieldName(sourceField.getFieldName());
		    targetField.setSnippetConfiguration(target);
		    	
		    fields.add(targetField);
		}
		    
		target.getFields().clear();
		target.getFields().addAll(fields);
	}

    @Override
    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_ALL_SEARCH_QUERIES, method = RequestMethod.GET)
    public void deleteAllSearchQueries() {
           searchQueryProcessService.deleteAllSearchQueries();
    }
}
