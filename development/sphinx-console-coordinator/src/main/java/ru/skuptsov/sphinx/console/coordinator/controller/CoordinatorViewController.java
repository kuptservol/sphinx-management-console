package ru.skuptsov.sphinx.console.coordinator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.skuptsov.sphinx.console.coordinator.annotation.ExecuteOnlyWithValidParams;
import ru.skuptsov.sphinx.console.coordinator.annotation.ExistsInDB;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.exception.SqlApplicationException;
import ru.skuptsov.sphinx.console.coordinator.health.ServerHealthChecker;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.common.DateDetailing;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.CollectionSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.SearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.SnippetSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryGrouped;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistoryPoint;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistorySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuerySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.search.query.SearchQueryProcessService;
import ru.skuptsov.sphinx.console.coordinator.service.api.CoordinatorView;
import ru.skuptsov.sphinx.console.coordinator.task.AddCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.AddDistributedCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.info.CollectionsInfoService;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateDistributedSphinxConfService;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxConfService;
import ru.skuptsov.sphinx.console.spring.service.api.*;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;
import ru.skuptsov.sphinx.console.util.sql.SqlParseService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class CoordinatorViewController extends AbstractCoordinatorController implements CoordinatorView {
	
	private static final Logger logger = LoggerFactory.getLogger(CoordinatorViewController.class);
	
	private static final int SNIPPET_QUERY_FIELDS_MIN_COUNT = 2;
	
	@Autowired
    private ProcessService processService;
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private ConfigurationTemplateService configurationTemplateService;
    @Autowired
    private AdminProcessService adminProcessService;
    @Autowired
	private GenerateSphinxConfService generateSphinxConfService;
    @Autowired
    private CollectionsInfoService collectionInfoService;
    @Autowired
	private GenerateDistributedSphinxConfService generateDistributedSphinxConfService;
    @Autowired
    private SqlParseService sqlParseService;
    @Autowired
    private SearchQueryProcessService searchQueryProcessService;

    

    @Resource(name = "messageSource")
    private MessageSource messageSource;


    
    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SERVERS, method = RequestMethod.GET)
    public @ResponseBody List<Server> getServers() {
        return serverService.getServers();
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SERVERS_BY_TYPE, method = RequestMethod.GET)
    public @ResponseBody List<Server> getServers(@PathVariable String processType) {
        return serverService.getServers(SphinxProcessType.valueOf(processType));
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.ADMIN_PROCESS, method = RequestMethod.GET)
    public @ResponseBody AdminProcess getAdminProcess(@PathVariable Long adminProcessId) {
        return adminProcessService.getAdminProcess(adminProcessId);
    }

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.PROCESSES, method = RequestMethod.GET)
	public @ResponseBody List<SphinxProcess> getProcesses() {
		return processService.getProcesses();
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTIONS, method = RequestMethod.GET)
	public @ResponseBody List<Collection> getCollections() {
		return collectionService.getCollections();
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION, method = RequestMethod.GET)
    public @ResponseBody Collection getCollection(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        return collectionService.getCollection(collectionName);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION_DATA, method = RequestMethod.POST)
    public @ResponseBody ListDataViewWrapper<List<CollectionWrapper>> getCollectionViewData(@RequestBody CollectionSearchParameters collectionSearchParameters) {
        List<CollectionWrapper> result = new LinkedList<CollectionWrapper>();
        collectionSearchParameters.setPagingEnabled(false);
        Long total = Long.valueOf(collectionService.getCollections(collectionSearchParameters).size());
        
        logger.info("TOTAL FOUND COLLECTIONS: " + total);
        
        collectionSearchParameters.setPagingEnabled(true);
        Map<String, CollectionInfoWrapper> collectionInfoMap = getCollectionsInfo();
        for(Collection collection : collectionService.getCollections(collectionSearchParameters)) {

            SphinxProcess indexSphinxProcess = processService.findIndexingProcess(collection.getName());
            CollectionWrapper collectionWrapper = new CollectionWrapper();
            collectionWrapper.setCollectionType(collection.getCollectionType());
            collectionWrapper.setCollection(collection);
            if (indexSphinxProcess != null) {
	            collectionWrapper.setIndexServer(indexSphinxProcess.getServer());
	            AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, indexSphinxProcess.getServer());
	            if(adminProcess != null) {
	                collectionWrapper.setIndexServerPort(adminProcess.getPort());
	            }
            }
            //SphinxProcess searchSphinxProcess = processService.findByCollectionNameAndType(collection.getName(), SphinxProcessType.SEARCHING);
            //collectionWrapper.setSearchServer(searchSphinxProcess.getServer()); //DOESN'T NEED TO VIEW COLLECTIONS LIST
            //collectionWrapper.setSearchConfigurationPort(getSearchConfigurationPortWrapper(searchSphinxProcess.getConfiguration().getId()));

            ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(collection.getName(), ScheduledTaskType.INDEXING_DELTA);
            if(scheduledTask != null) { //TODO
                CronScheduleWrapper cronScheduleWrapper = new CronScheduleWrapper();
                cronScheduleWrapper.setId(scheduledTask.getId());
                cronScheduleWrapper.setCronSchedule(scheduledTask.getCronSchedule());
                cronScheduleWrapper.setEnabled(BooleanUtils.toBoolean(scheduledTask.getIsEnabled()));
                collectionWrapper.setCronSchedule(cronScheduleWrapper);
            }

            ScheduledTask mainScheduledTask = scheduledTaskService.findByCollectionName(collection.getName(), ScheduledTaskType.MERGE_DELTA);
            if(mainScheduledTask != null) { //TODO
                CronScheduleWrapper mainCronScheduleWrapper = new CronScheduleWrapper();
                mainCronScheduleWrapper.setId(mainScheduledTask.getId());
                mainCronScheduleWrapper.setCronSchedule(mainScheduledTask.getCronSchedule());
                mainCronScheduleWrapper.setEnabled(BooleanUtils.toBoolean(mainScheduledTask.getIsEnabled()));
                collectionWrapper.setMainCronSchedule(mainCronScheduleWrapper);
            }

            collectionWrapper.setCollectionInfo(collectionInfoMap.get(collection.getName()));
            result.add(collectionWrapper);
        }

        return new ListDataViewWrapper(total, result);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SCHEDULED_TASKS, method = RequestMethod.GET)
	public @ResponseBody List<ScheduledTask> getScheduledTasks() {
		return scheduledTaskService.getScheduledTasks();
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_TEMPLATES, method = RequestMethod.GET)
	public @ResponseBody List<ConfigurationTemplate> getSearchConfigurationTemplates() {
        List<ConfigurationTemplate> templates = configurationTemplateService.getSearchConfigurationTemplates();
        List<ConfigurationTemplate> simpleTemplates = new ArrayList<ConfigurationTemplate>();
		
		if (templates != null) {
			for (ConfigurationTemplate template : templates) {
			    Set<ConfigurationFields> fields = template.getConfigurationFields();
			    if (fields != null) {
			        for (ConfigurationFields field : fields) {
			        	field.setConfigurationTemplate(null);
			        }
			    }
			    
			    if (template.getCollectionType() == CollectionRoleType.SIMPLE) {
			    	simpleTemplates.add(template);
			    }
			}
		}
		
		return simpleTemplates;
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.CONFIGURATION_TEMPLATE, method = RequestMethod.POST)
    public @ResponseBody ConfigurationTemplate getConfigurationTemplate(@RequestBody ConfigurationTemplate configurationTemplate) {
        return configurationTemplateService.getConfigurationTemplate(configurationTemplate.getName(), configurationTemplate.getType(), configurationTemplate.getCollectionType());
    }

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.CONFIGURATION_TEMPLATES, method = RequestMethod.GET)
	public @ResponseBody List<ConfigurationTemplate> getConfigurationTemplates() {
        List<ConfigurationTemplate> templates = configurationTemplateService.getConfigurationTemplates();
        List<ConfigurationTemplate> simpleTemplates = new ArrayList<ConfigurationTemplate>();
		
		if (templates != null) {
			for (ConfigurationTemplate template : templates) {
			    Set<ConfigurationFields> fields = template.getConfigurationFields();
			    if (fields != null) {
			        for (ConfigurationFields field : fields) {
			        	field.setConfigurationTemplate(null);
			        }
			    }
			    
			    if (template.getCollectionType() == CollectionRoleType.SIMPLE) {
			    	simpleTemplates.add(template);
			    }
			}
		}
		
		return simpleTemplates;
	}
	
	
	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.INDEXER_TEMPLATES, method = RequestMethod.GET)
	public @ResponseBody List<ConfigurationTemplate> getIndexerConfigurationTemplates() {
		List<ConfigurationTemplate> templates = configurationTemplateService.getIndexConfigurationTemplates();
		
		if (templates != null) {
			for (ConfigurationTemplate template : templates) {
			    Set<ConfigurationFields> fields = template.getConfigurationFields();
			    if (fields != null) {
			        for (ConfigurationFields field : fields) {
			        	field.setConfigurationTemplate(null);
			        }
			    }
			}
		}
		
		return templates;
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.CONFIGURATION, method = RequestMethod.GET)
	public @ResponseBody Configuration getConfiguration(@PathVariable Long processId) {
		return processService.getConfiguration(processId);
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.TASK_STATUS, method = RequestMethod.GET)
	public @ResponseBody TaskStatus getTaskStatus(@PathVariable String taskUid) {
		return tasksMapService.getTask(taskUid).getTaskStatus();
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.PROCESS_STATUS, method = RequestMethod.GET)
    public @ResponseBody ProcessStatus getProcessStatus(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
    	Collection collection = collectionService.getCollection(collectionName);
    	
    	Map<Long,ProcessStatus> result = new HashMap<Long, ProcessStatus>();
        for(Replica replica : collection.getReplicas()) {
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(collection.getName(), replica.getNumber());
            if (searchSphinxProcess == null) continue;
            Server server = searchSphinxProcess.getServer();
            AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
            MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
            String processName = collection.getName() + "_" + replica.getNumber();
            result.put(replica.getNumber(), monitoringService.isProcessAlive(server.getIp(), adminProcess.getPort(), processName));
        }
        
        boolean successProcessStatuses = false;
        boolean failureProcessStatuses = false;
        for(ProcessStatus processStatus : result.values()) {
            successProcessStatuses = successProcessStatuses || processStatus == ProcessStatus.SUCCESS;
            failureProcessStatuses = failureProcessStatuses || processStatus == ProcessStatus.FAILURE;
        }

        return (successProcessStatuses && failureProcessStatuses) ? ProcessStatus.FAILURE : successProcessStatuses ? ProcessStatus.SUCCESS : ProcessStatus.FAILURE;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION_SIZE, method = RequestMethod.GET)
    public @ResponseBody Long getCollectionSize(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, FIRST_REPLICA);
        Server server = searchSphinxProcess.getServer(); 
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
        
        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
        
        Integer port = getSearchConfigurationPortWrapper(searchSphinxProcess.getConfiguration().getId()).getSearchConfigurationPort();

        Long result = null;
        try {
            result = monitoringService.getCollectionSize(server.getIp(), adminProcess.getPort(), port, collectionName);
        } catch(Exception e) {
            result = null;
        }

        return result;
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.LOG, method = RequestMethod.POST)
	public  @ResponseBody List<ActivityLog> getActivityLog(@RequestBody ActivityLogSearchParameters parameter) {
		return activityLogService.getActivityLog(parameter);
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.LAST_SNIPPET_LOG_TASK_UID, method = RequestMethod.GET)
    public  @ResponseBody ResponseWrapper<String> getLastSnippetLogTaskUid(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {
        return new ResponseWrapper(activityLogService.getLastSnippetLogTaskUid(collectionName));
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.DATA_SOURCES, method = RequestMethod.GET)
    public @ResponseBody List<DataSource> getDataSources() {
        return dataSourceService.getDataSources();
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.DB_TABLES, method = RequestMethod.POST)
	public @ResponseBody List<DBTable> getDBTables(@RequestBody DataSource datasource) {
        logger.info("ABOUT TO RETRIEVE DATABASE SCHEME TABLES: " + datasource.getType());
        List<DBTable> dbTables = new ArrayList<DBTable>();
		
		org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("checkDataSource");
		
		dataSource.setDriverClassName(datasource.getType().getDriverClass());
		dataSource.setUrl(datasource.getType().getUrl(datasource));
		Connection connection = null;
		try {
			connection = dataSource.getConnection(datasource.getUser(), datasource.getPassword());
			
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			
			ResultSet res = databaseMetaData.getTables(null, null, null, new String[] {"TABLE"});
			
			while (res.next()) {
		         logger.info(
		            "   " + res.getString("TABLE_CAT") 
		           + ", " + res.getString("TABLE_SCHEM")
		           + ", " + res.getString("TABLE_NAME")
		           + ", " + res.getString("TABLE_TYPE")
		           + ", " + res.getString("REMARKS"));
		         
		         DBTable dbTable = new DBTable();
		         String tableScheme = datasource.getType() == DataSourceType.MYSQL ?res.getString("TABLE_CAT"):res.getString("TABLE_SCHEM");
		         dbTable.setName(tableScheme + "." + res.getString("TABLE_NAME"));
		         dbTable.setNameWithoutScheme(res.getString("TABLE_NAME"));
		         dbTables.add(dbTable);
		      }
			
			
			for (DBTable table : dbTables) {
				List<DBTableColumn> columns = new ArrayList<DBTableColumn>();
				res = databaseMetaData.getColumns(null, null, table.getNameWithoutScheme(), null);

				
				while (res.next()) {
					logger.info(
					           "  " + res.getString("TABLE_SCHEM")
					           + ", " + res.getString("TABLE_NAME")
					           + ", " + res.getString("COLUMN_NAME")
					           + ", " + res.getString("TYPE_NAME")
					           + ", " + res.getInt("COLUMN_SIZE")
					           + ", " + res.getString("NULLABLE")); 

			         
			         DBTableColumn column = new DBTableColumn();
			         column.setName(res.getString("COLUMN_NAME"));
			         column.setType(res.getString("TYPE_NAME"));
			         
			         columns.add(column);
			      }
				
				table.setColumns(columns);
			}

			
		} catch (SQLException e) {
			logger.error("could not connect to DB: ", e);
		    throw new ApplicationException(e);	
		} finally {
			try {
				if (connection != null) {
				    connection.close();
				}
			} catch (SQLException e) {
				
			}
		}
		
		return dbTables;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SERVER_STATUS, method = RequestMethod.POST)
	public @ResponseBody ServerStatus getServerStatus(@RequestBody Server server) {
		Server currentServer = null;

		if (server.getId() != null) {
			currentServer = serverService.getServer(server.getId());
		} else if (server.getName() != null) {
			currentServer = serverService.getServer(server.getName());
		}
		
		if (currentServer == null) {
			throw new ApplicationException("server has not been found.");
		}
		
        AdminProcess coordinatorAdminProcess = serverService.getAdminProcess(ProcessType.COORDINATOR, currentServer);
        AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, currentServer);
        AdminProcess indexAdminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, currentServer);
		
        if (coordinatorAdminProcess == null && searchAdminProcess == null && indexAdminProcess == null) {
        	return ServerStatus.STOPPED; 
        }
		
		if (searchAdminProcess != null ) {
			String searchAgentAddress = jmxService.getConnectorUrl(searchAdminProcess);
			ServerHealthChecker serverHealthChecker = (ServerHealthChecker)ApplicationContextProvider.getBean("serverHealthChecker");
			if (!serverHealthChecker.checkHealth(searchAgentAddress)) {
				return ServerStatus.STOPPED;
			}
		} 
		
		if (indexAdminProcess != null ) {
			String indexAgentAddress = jmxService.getConnectorUrl(indexAdminProcess);
			ServerHealthChecker serverHealthChecker = (ServerHealthChecker)ApplicationContextProvider.getBean("serverHealthChecker");
			if (!serverHealthChecker.checkHealth(indexAgentAddress)) {
				return ServerStatus.STOPPED;
			}
		} 
		
		return ServerStatus.RUNNING;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.CURRENTLY_INDEXING, method = RequestMethod.POST)
	public @ResponseBody ResponseWrapper<Boolean> isCurrentlyIndexing(@PathVariable String collectionName, @PathVariable SphinxProcessType type) {
		SphinxProcess sphinxProcess = processService.findByCollectionNameAndType(collectionName, type).get(0);
        Server server = sphinxProcess.getServer();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
        
        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        Boolean result = false;
        try {
            result = monitoringService.isCurrentlyIndexing(server.getIp(), adminProcess.getPort(), collectionName + "_" + FIRST_REPLICA);
        } catch(Exception e) {
            result = false;
        }

        return new ResponseWrapper<Boolean>(result);
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.CURRENTLY_INDEXING_DELTA, method = RequestMethod.POST)
    public @ResponseBody ResponseWrapper<Boolean> isCurrentlyIndexingDelta(@PathVariable String collectionName) {
        SphinxProcess sphinxProcess = processService.findByCollectionNameAndType(collectionName, SphinxProcessType.INDEXING).get(0);
        Server server = sphinxProcess.getServer();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);

        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        Boolean result = false;
        try {
            result = monitoringService.isCurrentlyIndexing(server.getIp(), adminProcess.getPort(), collectionName + "_" + FIRST_REPLICA, collectionName + "_" + IndexType.DELTA.getTitle());
        } catch(Exception e) {
            result = false;
        }

        return new ResponseWrapper<Boolean>(result);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.CURRENTLY_MERGING, method = RequestMethod.POST)
    public @ResponseBody ResponseWrapper<Boolean> isCurrentlyMerging(@PathVariable String collectionName) {
        SphinxProcess sphinxProcess = processService.findByCollectionNameAndType(collectionName, SphinxProcessType.INDEXING).get(0);
        Server server = sphinxProcess.getServer();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);

        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        Boolean result = false;
        try {
            result = monitoringService.isCurrentlyMerging(server.getIp(), adminProcess.getPort(), collectionName + "_" + FIRST_REPLICA);
        } catch(Exception e) {
            result = false;
        }

        return new ResponseWrapper<Boolean>(result);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SERVER_ADMIN_PROCESSES, method = RequestMethod.GET)
    public @ResponseBody List<AdminProcess> getAdminProcesses(@PathVariable Long serverId) {
        return adminProcessService.getAdminProcesses(serverId);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.TASK_NAMES, method = RequestMethod.GET)
    public @ResponseBody List<TaskNameWrapper> getTaskNames() {
        List<TaskNameWrapper> result = new ArrayList<TaskNameWrapper>();
        for(TaskName taskName : TaskName.values()){
            result.add(new TaskNameWrapper(taskName, taskName.getTitle()));
        }
        return result;
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.TASKS, method = RequestMethod.POST)
	public @ResponseBody ListDataViewWrapper<List<TaskDataViewWrapper>> getTasks(@RequestBody ActivityLogSearchParameters param) {
  		logger.info("ABOUT TO RETRIEVE TASKS: " + param);
  		
  		if (param != null) {
  			logger.info("PAGE: " + param.getPage());
  			logger.info("START: " + param.getStart());
  			logger.info("LIMIT: " + param.getLimit());
  			logger.info("COLLECTION TERM: " + param.getCollectionName());
            logger.info("TASK NAMES: " + param.getTaskNames());
  			logger.info("TASK STATUS: " + param.getTaskStatus());
  			logger.info("DATE FROM: " + param.getDateFrom());
  			logger.info("DATE TO: " + param.getDateTo());
  		}
  		
		List<ActivityLog> activityLogs = activityLogService.getActivityLog(param);
  		
  		List<TaskDataViewWrapper> tasks = new ArrayList<TaskDataViewWrapper>();
  		
  		if (activityLogs != null) {
  			for (ActivityLog log : activityLogs) {
  				TaskDataViewWrapper task = new TaskDataViewWrapper();
  				
  				task.setTaskUid(log.getTaskUid());
  				task.setTaskName(log.getTaskName());
  				task.setCollectionName(log.getCollection() != null ? log.getCollection().getName() : null);
  				task.setStartTime(log.getTaskStartTime());
  				task.setEndTime(log.getTaskEndTime());
  				task.setStage(log.getOperationType() != null?messageSource.getMessage(log.getOperationType(), null, new Locale("ru", "RU")):"");
  				task.setStatus(log.getTaskStatus());
  				
  				tasks.add(task);
  			}
  		}
  		
  		Long total = activityLogService.countActivityLog(param);
  		
  		logger.info("FOUND TOTAL TASKS: " + total);
  		
  		ListDataViewWrapper<List<TaskDataViewWrapper>> wrapper = new ListDataViewWrapper<List<TaskDataViewWrapper>>(total, tasks);
  		
		return wrapper;
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SERVERS, method = RequestMethod.POST)
    public @ResponseBody ListDataViewWrapper<List<Server>> getServers(@RequestBody final SearchParameters param) {
        logger.info("ABOUT TO RETRIEVE SERVERS: " + param);

        List<Server> servers = serverService.getServers();

        if (servers==null) return new ListDataViewWrapper(0L,new ArrayList<Server>());

        if (param != null) {
            logger.info("PAGE SIZE: " + param.getPage());
            logger.info("START: " + param.getStart());
            logger.info("LIMIT: " + param.getLimit());
            final String serverName = param.getValueFilterByName("serverName");
            if(serverName!=null&&serverName.length()>0){
                CollectionUtils.filter(servers,new Predicate(){
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Server)object).getName().toLowerCase().indexOf(serverName.toLowerCase())>-1;
                    }
                });
            }
            if(param.getLimit()!=null&&servers.size()>param.getLimit()){
                int startIndexPage = (param.getPage()-1)*param.getLimit();
                int endIndexPage = (param.getPage()*param.getLimit());
                return new ListDataViewWrapper<List<Server>>(Long.valueOf(servers.size()),servers.subList(startIndexPage,(endIndexPage>=servers.size()?servers.size():endIndexPage)));
            }
            final String uniqueServerName = param.getValueFilterByName("uniqueServerName");
            if(uniqueServerName!=null&&uniqueServerName.length()>0){
                CollectionUtils.filter(servers,new Predicate(){
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Server)object).getName().equals(uniqueServerName);
                    }
                });
            }
            final String uniqueServerIp = param.getValueFilterByName("uniqueServerIp");
            if(uniqueServerIp!=null&&uniqueServerIp.length()>0){
                CollectionUtils.filter(servers,new Predicate(){
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Server)object).getIp().equals(uniqueServerIp);
                    }
                });
            }

            return new ListDataViewWrapper<List<Server>>(Long.valueOf(servers.size()),servers);
        }

        return new ListDataViewWrapper<List<Server>>(Long.valueOf(servers.size()),servers);
    }


	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.TASK_LOG, method = RequestMethod.POST)
	public @ResponseBody ListDataViewWrapper<List<TaskDataViewWrapper>> getTaskLogs(@RequestBody TaskLogsSearchParameters param) {
		logger.info("ABOUT TO RETRIEVE TASK LOG: " + param);

		if (param != null) {
  			logger.info("PAGE: " + param.getPage());
  			logger.info("START: " + param.getStart());
  			logger.info("LIMIT: " + param.getLimit());
  			logger.info("TASK UID: " + param.getTaskUid());
            logger.info("REPLICA NUMBER: " + param.getReplicaNumber());
            logger.info("PROCESS ID: " + param.getProcessId());
  		}
  		
		List<ActivityLog> activityLogs = activityLogService.getTaskLog(param);
  		
  		List<TaskDataViewWrapper> tasks = new ArrayList<TaskDataViewWrapper>();
  		
  		if (activityLogs != null) {
            activityLogs.remove(null);
  			for (ActivityLog log : activityLogs) {
  				TaskDataViewWrapper task = new TaskDataViewWrapper();
  				
  				task.setId(log.getId());
  				task.setTaskUid(log.getTaskUid());
  				task.setTaskName(log.getTaskName());
  				task.setCollectionName(log.getCollection().getName());
                task.setReplicaNumber(log.getReplicaNumber());
                if(log.getProcess() != null) {
                    task.setProcessId(log.getProcess().getId());
                }
                task.setOperationType(log.getOperationType());
  				task.setServerName(log.getServerName());
  				task.setStartTime(log.getStartTime());
  				task.setEndTime(log.getEndTime());
  				task.setStage(log.getOperationType() != null?messageSource.getMessage(log.getOperationType(), null, new Locale("ru", "RU")):"");
                task.setStageStatus(log.getStatus());
                task.setStatus(log.getTaskStatus());
  				
  				tasks.add(task);
  			}
  		}
  		
  		Long total = activityLogService.countTaskLog(param);
  		
  		logger.info("FOUND TOTAL TASKS: " + total);
  		
  		ListDataViewWrapper<List<TaskDataViewWrapper>> wrapper = new ListDataViewWrapper<List<TaskDataViewWrapper>>(total, tasks);
  		
		return wrapper;
	}


    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.TASK_COMPLETE, method = RequestMethod.GET)
    public @ResponseBody String getTaskComplete(@PathVariable String taskUid) {
        TaskLogsSearchParameters param = new TaskLogsSearchParameters();
        param.setTaskUid(taskUid);

        logger.info("ABOUT TO RETRIEVE TASK LOG: " + param);

        if (param != null) {
            logger.info("PAGE: " + param.getPage());
            logger.info("START: " + param.getStart());
            logger.info("LIMIT: " + param.getLimit());
            logger.info("TASK UID: " + param.getTaskUid());
            logger.info("REPLICA NUMBER: " + param.getReplicaNumber());
            logger.info("PROCESS ID: " + param.getProcessId());
        }

        List<ActivityLog> activityLogs = activityLogService.getTaskLog(param);

        if(activityLogs != null && activityLogs.size() > 0) {
            for (ActivityLog activityLog : activityLogs) {
                if ("COMPLETED".equals(activityLog.getOperationType())) {
                    return TaskRunningStatus.COMPLETE.name();
                } else if(StageStatus.FAILURE.equals(activityLog.getStatus())) {
                    return TaskRunningStatus.FAIL.name();
                }
            }

            return TaskRunningStatus.RUNNING.name();
        } else {

            return TaskRunningStatus.EMPTY.name();
        }
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.ADMIN_PROCESSES, method = RequestMethod.POST)
    public @ResponseBody ListDataViewWrapper<List<AdminProcess>> getAdminProcesses(@RequestBody final SearchParameters param) {
        logger.info("ABOUT TO RETRIEVE ADMIN PROCESSES: " + param);

        List<AdminProcess> adminProcesses = null;

        if (param != null) {
            logger.info("PAGE SIZE: " + param.getPage());
            logger.info("START: " + param.getStart());
            logger.info("LIMIT: " + param.getLimit());

            final String serverId = param.getValueFilterByName("serverId");
            adminProcesses = adminProcessService.getAdminProcesses(Long.valueOf(serverId));
            if(param.getLimit() != null &&
                    adminProcesses.size() > param.getLimit()){
                int startIndexPage = (param.getPage()-1) * param.getLimit();
                int endIndexPage = (param.getPage() * param.getLimit());
                return new ListDataViewWrapper<List<AdminProcess>>(Long.valueOf(adminProcesses.size()), adminProcesses.subList(startIndexPage,(endIndexPage>=adminProcesses.size()?adminProcesses.size():endIndexPage)));
            }

            return new ListDataViewWrapper<List<AdminProcess>>(Long.valueOf(adminProcesses.size()), adminProcesses);
        }
        adminProcesses = adminProcessService.getAdminProcesses((Long)null);
        if (adminProcesses == null) {

            return new ListDataViewWrapper(0L, new ArrayList<Server>());
        } else {

            return new ListDataViewWrapper<List<AdminProcess>>(Long.valueOf(adminProcesses.size()), adminProcesses);
        }
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SPHINX_CONF, method = RequestMethod.POST)
	public @ResponseBody byte[] getSphinxConf(@PathVariable String collectionName, @PathVariable Long replicaNumber) {
        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        return monitoringService.getRealSphinxConf(collectionName, replicaNumber);
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.ADMIN_PROCESS_STATUS, method = RequestMethod.POST)
	public @ResponseBody ServerStatus getAdminProcessStatus(@RequestBody AdminProcess adminProcess, HttpServletRequest request) {
        logger.info("ABOUT TO GET ADMIN PROCESS STATUS: " + adminProcess.getPort());
    	if(adminProcess.getId()!=null){
		    adminProcess = adminProcessService.getAdminProcess(adminProcess.getId());
            if (adminProcess == null) {
                throw new ApplicationException("admin process has not been found: " + adminProcess.getId());
            }
        }

		Server currentServer = adminProcess.getServer();
		
		if (currentServer == null) {
            throw new ApplicationException("server has not been found, for admin process: " + adminProcess.getId());
        }
		
		String address = jmxService.getConnectorUrl(adminProcess);
		ServerHealthChecker serverHealthChecker = ApplicationContextProvider.getBean("serverHealthChecker");
		if (!serverHealthChecker.checkHealth(address)) {
			return ServerStatus.STOPPED;
		}
		
		return ServerStatus.RUNNING;
	}
    
    private DistributedCollectionWrapper getDistributedCollectionWrapperByName(String collectionName) {
    	DistributedCollectionWrapper collectionWrapper = new DistributedCollectionWrapper();
    	
    	Collection collection = collectionService.getCollection(collectionName);
    	 
    	collectionWrapper.setCollection(collection);
    	
    	SphinxProcess process = processService.findSearchProcess(collectionName, FIRST_REPLICA);
    	
    	if (process == null) {
    		throw new ApplicationException("Sphinx process has noot been retrieved");
    	}
    	
    	collectionWrapper.setSearchConfiguration(process.getConfiguration());
    	
    	collectionWrapper.setSearchConfigurationPort(getSearchConfigurationPortWrapper(process.getConfiguration().getId()));
		
    	collectionWrapper.setSearchServer(process.getServer());
    	
        Set<DistributedCollectionNode> nodes = collection.getDistributedCollectionNodes();
        
        List<SimpleCollectionWrapper> wrappers = new ArrayList<SimpleCollectionWrapper>();
		
		for (DistributedCollectionNode node : nodes) {
			SimpleCollectionWrapper wrapper = new SimpleCollectionWrapper();
			wrapper.setCollectionName(node.getSimpleCollection().getName());
			
			TreeSet<SimpleCollectionReplicaWrapper> agentWrappers = new TreeSet<SimpleCollectionReplicaWrapper>();
			Set<DistributedCollectionAgent> agents = node.getDistributedCollectionAgents();
			
			for (DistributedCollectionAgent agent : agents) {
				SimpleCollectionReplicaWrapper agentWrapper = new SimpleCollectionReplicaWrapper();
				
				ConfigurationFields field = configurationFieldsService.getDistributedPort(agent.getSphinxProcess().getConfiguration().getId());
				if (field != null) {
				    agentWrapper.setNodeDistribPort(new Integer(field.getFieldValue()));
				}
				agentWrapper.setNodeHost(agent.getSphinxProcess().getServer().getIp());
				
				agentWrappers.add(agentWrapper);
			}
			
			wrapper.setAgents(new ArrayList<SimpleCollectionReplicaWrapper>(agentWrappers));
			wrappers.add(wrapper);
		}
        
    	
    	collectionWrapper.setNodes(wrappers);
    	
    	return collectionWrapper;
    }
    
    private SnippetConfigurationWrapper getSnippetConfigurationWrapperByName(String collectionName) {
    	SnippetConfigurationWrapper snippetConfigurationWrapper = new SnippetConfigurationWrapper();
    	
    	Collection collection = collectionService.getCollection(collectionName);
    	
    	
    	snippetConfigurationWrapper.setCollectionName(collection.getName());
    	
    	snippetConfigurationWrapper.setSnippetConfiguration(collection.getSnippetConfiguration());
    	ScheduledTask scheduledTask = collection.getSnippetConfiguration().getScheduledTask();
    	CronScheduleWrapper cron = null;
        if (scheduledTask != null) {
            cron = new CronScheduleWrapper();
            cron.setCronSchedule(scheduledTask.getCronSchedule());
            cron.setEnabled(scheduledTask.getIsEnabled());
        }
    	
    	snippetConfigurationWrapper.setCron(cron);
    	
    	return snippetConfigurationWrapper;
    }


    private CollectionWrapper getCollectionWrapperByName(String collectionName) {
        CollectionWrapper collectionWrapper = new CollectionWrapper();

        Collection collection = collectionService.getCollection(collectionName);
        List<SphinxProcess> searchSphinxProcesses = processService.findSearchProcesses(collection.getName()); //a lot the same processes //TODO
        SphinxProcess indexSphinxProcess = processService.findIndexingProcess(collection.getName()); //should be one process with type INDEXING
        ScheduledTask schedule = scheduledTaskService.findByCollectionName(collectionName, ScheduledTaskType.INDEXING_DELTA);
        ScheduledTask mergeDeltaSchedule = scheduledTaskService.findByCollectionName(collectionName, ScheduledTaskType.MERGE_DELTA);

        Configuration searchConfiguration = (searchSphinxProcesses != null && !searchSphinxProcesses.isEmpty() && searchSphinxProcesses.get(0) != null) ? searchSphinxProcesses.get(0).getConfiguration() : null;
        Configuration indexConfiguration = indexSphinxProcess != null ? indexSphinxProcess.getConfiguration() : null;

        CronScheduleWrapper cronSchedule = null;
        if (schedule != null) {
            cronSchedule = new CronScheduleWrapper();
            cronSchedule.setCronSchedule(schedule.getCronSchedule());
            cronSchedule.setEnabled(schedule.getIsEnabled());
        }
        CronScheduleWrapper mergeDeltaCronSchedule = null;
        if (mergeDeltaSchedule != null) {
            mergeDeltaCronSchedule = new CronScheduleWrapper();
            mergeDeltaCronSchedule.setCronSchedule(mergeDeltaSchedule.getCronSchedule());
            mergeDeltaCronSchedule.setEnabled(mergeDeltaSchedule.getIsEnabled());
        }
        collectionWrapper.setCollection(collection);
        if (searchSphinxProcesses != null && !searchSphinxProcesses.isEmpty() && searchSphinxProcesses.get(0) != null) {
            collectionWrapper.setSearchServer(searchSphinxProcesses.get(0).getServer());
        }
        if (indexSphinxProcess != null) {
            collectionWrapper.setIndexServer(indexSphinxProcess.getServer());
        }
        collectionWrapper.setCronSchedule(cronSchedule);
        collectionWrapper.setMainCronSchedule(mergeDeltaCronSchedule);
        if (searchConfiguration != null) {
            collectionWrapper.setSearchConfigurationPort(getSearchConfigurationPortWrapper(searchConfiguration.getId()));
            collectionWrapper.setDistributedConfigurationPort(getDistributedConfigurationPortWrapper(searchConfiguration.getId()));
        }
        collectionWrapper.setSearchConfiguration(searchConfiguration);
        collectionWrapper.setIndexConfiguration(indexConfiguration);

        return collectionWrapper;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION_WRAPPER_JSON, method = RequestMethod.POST)
    public @ResponseBody byte[] getCollectionWrapperJson(@PathVariable String collectionName) {
        CollectionWrapper collectionWrapper = getCollectionWrapperByName(collectionName);

        byte[] collectionWrapperJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            collectionWrapperJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionWrapper).getBytes();
        } catch (JsonProcessingException e) {
            logger.error("Error during getCollectionWrapperJson", e);
        }

        return collectionWrapperJson;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SERVER_WRAPPER_JSON, method = RequestMethod.POST)
    public @ResponseBody byte[] getServerWrapperJson(@PathVariable String serverName) {
        ServerWrapper serverWrapper = new ServerWrapper();

        List<AdminProcess> adminProcesses = adminProcessService.getAdminProcesses(serverName);
        serverWrapper.setAdminProcesses(adminProcesses);

        Server server = serverService.getServer(serverName);
        serverWrapper.setServer(server);

        byte[] serverWrapperJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            serverWrapperJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serverWrapper).getBytes();
        } catch (JsonProcessingException e) {
            logger.error("Error during getServerWrapperJson", e);
        }

        return serverWrapperJson;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SNIPPET_WRAPPER_JSON, method = RequestMethod.POST)
    public @ResponseBody byte[] getSnippetWrapperJson(@PathVariable String collectionName) {
        SnippetConfigurationWrapper snippetConfigurationWrapper = getSnippetConfigurationWrapper(collectionName);

        byte[] snippetWrapperJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            snippetWrapperJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(snippetConfigurationWrapper).getBytes();
        } catch (JsonProcessingException e) {
            logger.error("Error during getSnippetWrapperJson", e);
        }

        return snippetWrapperJson;
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION_WRAPPER, method = RequestMethod.POST)
	public @ResponseBody ResponseWrapper<CollectionWrapper> getCollectionWrapper(@PathVariable String collectionName) {
		return new ResponseWrapper<CollectionWrapper>(getCollectionWrapperByName(collectionName));
	}
	
	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SPHINX_CONF_PREVIEW, method = RequestMethod.POST)
	public @ResponseBody byte[] getSphinxConfPreview(@RequestBody CollectionWrapper collectionWrapper) {
		
		AddCollectionTask task = new AddCollectionTask();
		task.setType(collectionWrapper.getCollection().getType());
		logger.info("COLLECTIOn TYPE: " + task.getType());
		
		task.setReplicaNumber(FIRST_REPLICA);

        if(collectionWrapper.getIndexConfiguration() != null){
            task.setMainSqlQuery(collectionWrapper.getIndexConfiguration().getMainSqlQuery());
            task.setTableName(collectionWrapper.getTableName());
            task.setDeltaSqlQuery(collectionWrapper.getIndexConfiguration().getDeltaSqlQuery());
        }

        task.setIndexConfiguration(collectionWrapper.getIndexConfiguration());
        if(collectionWrapper.getSearchConfigurationPort() != null){
            task.setSearchConfigurationPort(collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
        }
        if(collectionWrapper.getDistributedConfigurationPort() != null){
        	logger.info("DISTRIBUTED PORT: " + collectionWrapper.getDistributedConfigurationPort().getDistributedConfigurationPort());
            task.setDistributedConfigurationPort(collectionWrapper.getDistributedConfigurationPort().getDistributedConfigurationPort());
        }
        task.setSearchConfiguration(collectionWrapper.getSearchConfiguration());
		task.setCollection(collectionWrapper.getCollection());
		
		String content = generateSphinxConfService.generateContent(task, SphinxProcessType.INDEXING);
		if (content != null) {

			return content.getBytes();
		}

		return null;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.TASK_ERROR_DESCRIPTION, method = RequestMethod.POST)
	public @ResponseBody byte[] getTaskErrorDescription(@PathVariable Long taskId) {
		logger.info("ABOUt TO RETRIEVE ERROR DESCRIPTION, TASK ID: " + taskId);
		ActivityLog log = activityLogService.findById(taskId);
		logger.info("RETRIEVED LOG: " + (log != null ? log.getId() : null));
		if (log.getExceptionText() != null) {
			return log.getExceptionText().getBytes();
		}

		return null;
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.TASK_ERROR_DESCRIPTION_BY_UID, method = RequestMethod.POST)
    public @ResponseBody byte[] getTaskErrorDescription(@PathVariable String taskUid) {
        logger.info("ABOUt TO RETRIEVE ERROR DESCRIPTION, TASK UID: " + taskUid);
        TaskLogsSearchParameters parameters = new TaskLogsSearchParameters();
        parameters.setTaskUid(taskUid);
        List<ActivityLog> logs = activityLogService.getTaskLog(parameters);

        byte[] result = null;
        for(ActivityLog log : logs) {
            if(log.getExceptionText()!= null) {
                result = log.getExceptionText().getBytes();
            }
        }

        return result;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION_NAMES_USE_TEMPLATE, method = RequestMethod.GET)
    public @ResponseBody List<String> getCollectionsUseTemplate(@PathVariable Long templateId){
        List<Collection> collections = collectionService.getNameCollectionsByTemplateId(templateId);
        if(collections!=null){
            List<String> collectionNames = new ArrayList<String>();
            for(Collection collection : collections){
                collectionNames.add(collection.getName());
            }

            return collectionNames;
        }

        return null;
    }

	/**
	 * {
    "sqlQuery": "select * from sphinx.console.SERVER",
    "type": "MYSQL",
    "host": "192.168.211.111",
    "port": "3306",
    "user": "root",
    "password": "root",
    "sqlDb": "sphinx.console"
    }
	 */
	private List<DBTableColumn> getQueryColumns(DataSource source, String sqlQuery) {
        List<DBTableColumn> metaData = new ArrayList<DBTableColumn>();
		
		org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("checkDataSource");
		
		dataSource.setDriverClassName(source.getType().getDriverClass());
		dataSource.setUrl(source.getType().getUrl(source));
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection(source.getUser(), source.getPassword());
			statement = connection.createStatement();
			
			if (sqlQuery != null && sqlQuery.endsWith(";")) {
				sqlQuery = sqlQuery.substring(0, sqlQuery.lastIndexOf(";"));
			}

			if (source.getType() == DataSourceType.MSSQL || source.getType() == DataSourceType.PGSQL) {
				sqlQuery = sqlQuery + " limit 1";
			}

			sqlQuery = sqlQuery.replace("$start", "1");
			sqlQuery = sqlQuery.replace("$end", "1");

			logger.info("QUERY TO BE EXECUTED: " + sqlQuery);
			
			ResultSet res = statement.executeQuery(sqlQuery);
			ResultSetMetaData rmd = res.getMetaData();

			for (int i = 1; i <= rmd.getColumnCount(); i++) {
		        String name = rmd.getColumnName(i);
		        String type = rmd.getColumnTypeName(i);
		        int size = rmd.getPrecision(i);
		        logger.info("   " + name + ", " + type + ", " +size);

		        DBTableColumn column = new DBTableColumn();
		        column.setName(name);
		        column.setType(type);

		        metaData.add(column);
		      }
		} catch (SQLException e) {
			logger.error("could not connect to DB: ", e);
		    throw new SqlApplicationException(e);
		} finally {
			try {
				if (connection != null) {
				    connection.close();
				}
			} catch (SQLException e) {
				
			}
		}
		
		return metaData;
	}

    //@RequestMapping(value = CoordinatorViewRestURIConstants.VALIDATE_KILL_QUERY, method = RequestMethod.POST)
    private boolean validateKillQuery(SourceWrapper sourceWrapper) {
        DataSource source = sourceWrapper.getDatasource();
        String sqlQuery = sourceWrapper.getKillSqlQuery();

        org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("checkDataSource");

        dataSource.setDriverClassName(source.getType().getDriverClass());
        dataSource.setUrl(source.getType().getUrl(source));
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection(source.getUser(), source.getPassword());
            statement = connection.createStatement();

            if (source.getType() == DataSourceType.MSSQL || source.getType() == DataSourceType.PGSQL) {
                sqlQuery = sqlQuery + " limit 1";
            }

            statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            logger.error("could not connect to DB: ", e);
            throw new SqlApplicationException(e, messageSource.getMessage("KILL_SQL", null, new Locale("ru", "RU")));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }

        return true;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.IS_MAPPING_CHANGED, method = RequestMethod.POST)
    public @ResponseBody ResponseWrapper<Boolean> isMappingChanged(@RequestBody Configuration configuration) {
        return new ResponseWrapper<Boolean>(checkIfMappingChanged(configuration));
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.QUERY_META_DATA, method = RequestMethod.POST)
    public @ResponseBody List<DBTableColumn> getQueryMetaData(@RequestBody SourceWrapper sourceWrapper) {
        logger.info("ABOUT TO RETRIEVE SQL QUERY META DATA: " + sourceWrapper.getMainSqlQuery());
        final String DIFFERENT_COLUMNS_MESSAGE = messageSource.getMessage("DIFFERENT_COLUMNS_MESSAGE", null, new Locale("ru", "RU"));
        if (sourceWrapper.getMainSqlQuery() == null && sourceWrapper.getTableName() == null) {
            throw new ApplicationException(messageSource.getMessage("MAIN_SQL_NOT_SPECIFIED", null, new Locale("ru", "RU")));
        }
        String mainSqlQuery = sourceWrapper.getTableName() != null ? "select * from " + sourceWrapper.getTableName() : sourceWrapper.getMainSqlQuery();
        
        List<DBTableColumn> mainDbColumns = null;
        try {
        	sqlParseService.parseSqlQuery(mainSqlQuery);
        	mainDbColumns = getQueryColumns(sourceWrapper.getDatasource(), mainSqlQuery);
        } catch(SqlApplicationException e) {
            e.setDescription(messageSource.getMessage("MAIN_SQL", null, new Locale("ru", "RU")));
            throw e;
        }

        if (sourceWrapper.getDeltaSqlQuery() != null) {
            List<DBTableColumn> deltaDbColumns = null;
            try {
            	sqlParseService.parseSqlQuery(sourceWrapper.getDeltaSqlQuery());
            	deltaDbColumns = getQueryColumns(sourceWrapper.getDatasource(), sourceWrapper.getDeltaSqlQuery());
            } catch(SqlApplicationException e) {
                e.setDescription(messageSource.getMessage("DELTA_SQL", null, new Locale("ru", "RU")));
                throw e;
            }

            if (mainDbColumns.size() != deltaDbColumns.size()) {
                throw new ApplicationException(DIFFERENT_COLUMNS_MESSAGE);
            } else {
                List<String> mainDbColumnNames = new LinkedList<String>();
                List<String> deltaDbColumnNames = new LinkedList<String>();
                for (DBTableColumn dbColumn : mainDbColumns) {
                    mainDbColumnNames.add(dbColumn.getName());
                }
                for (DBTableColumn dbColumn : deltaDbColumns) {
                    deltaDbColumnNames.add(dbColumn.getName());
                }
                boolean isEqualsColumns = CollectionUtils.isEqualCollection(mainDbColumnNames, deltaDbColumnNames);
                if (!isEqualsColumns) {
                    throw new ApplicationException(DIFFERENT_COLUMNS_MESSAGE);
                }
            }
            if(sourceWrapper.getKillSqlQuery() != null) {
                validateKillQuery(sourceWrapper);
            }
        }

        return mainDbColumns;
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.QUERY_COLLECTIONS_INFO, method = RequestMethod.GET)
	public @ResponseBody ConcurrentHashMap<String, CollectionInfoWrapper> getCollectionsInfo() {
		return collectionsInfoMap;
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.QUERY_COLLECTIONS_INFO_FROM_AGENT, method = RequestMethod.GET)
    public @ResponseBody ConcurrentHashMap<String, CollectionInfoWrapper> getCollectionsInfoFromAgent() {
        collectionInfoService.process();

        return collectionsInfoMap;
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.QUERY_SERVERS_INFO, method = RequestMethod.GET)
	public @ResponseBody ConcurrentHashMap<String, ServerInfoWrapper> getServersInfo() {
		return serversInfoMap;
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.DELTAS, method = RequestMethod.GET)
    public @ResponseBody Set<Delta> getDeltas(@PathVariable String collectionName) {
        logger.info("ABOUT TO RETRIEVE DELTAS BY COLLECTION: " + collectionName);
        Delta delta = collectionService.getCollection(collectionName).getDelta();
       
        Set<Delta> deltas = new HashSet<Delta>();
        
        if (delta != null) {
        	deltas.add(delta);
        }
        
        return deltas;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.REPLICAS, method = RequestMethod.GET)
    public @ResponseBody List<Replica> getReplicas(@PathVariable String collectionName) {
        logger.info("ABOUT TO RETRIEVE REPLICAS BY COLLECTION: " + collectionName);
        Set<Replica> replicas = collectionService.getCollection(collectionName).getReplicas();
        return new LinkedList<Replica>(replicas);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.REPLICAS_DATA, method = RequestMethod.GET)
    public @ResponseBody ListDataViewWrapper<List<ReplicaWrapper>> getReplicasData(@PathVariable String collectionName) {
        logger.info("ABOUT TO RETRIEVE REPLICAS DATA BY COLLECTION: " + collectionName);
        List<ReplicaWrapper> result = new LinkedList<ReplicaWrapper>();
        List<SphinxProcess> searchSphinxProcesses = processService.findByCollectionNameAndType(collectionName, SphinxProcessType.SEARCHING);
        for(SphinxProcess searchSphinxProcess: searchSphinxProcesses) {
            ReplicaWrapper replicaWrapper = new ReplicaWrapper();
            replicaWrapper.setCollectionName(collectionName);
            replicaWrapper.setReplicaNumber(searchSphinxProcess.getReplica().getNumber());
            
            replicaWrapper.setCollectionType(searchSphinxProcess.getCollection().getCollectionType());
            
            logger.info("CONFIGURATION ID: " + searchSphinxProcess.getConfiguration().getId());
            
            replicaWrapper.setSearchPort(getSearchConfigurationPortWrapper(searchSphinxProcess.getConfiguration().getId()).getSearchConfigurationPort());
            
            DistributedConfigurationPortWrapper distributedPort = getDistributedConfigurationPortWrapper(searchSphinxProcess.getConfiguration().getId());
            if (distributedPort != null) {
                replicaWrapper.setDistributedPort(distributedPort.getDistributedConfigurationPort());
            }
            replicaWrapper.setServer(searchSphinxProcess.getServer());
            
            MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
            
            
            Server server = searchSphinxProcess.getServer(); 
            AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
            
            replicaWrapper.setSearchServerStatus(monitoringService.isProcessAlive(server.getIp(), adminProcess.getPort(),  collectionName + "_" + searchSphinxProcess.getReplica().getNumber()) == ProcessStatus.SUCCESS ?true:false);
            
            result.add(replicaWrapper);
        }

        return new ListDataViewWrapper<List<ReplicaWrapper>>(Long.valueOf(result.size()), result);
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.LAST_REPLICA, method = RequestMethod.POST)
	public @ResponseBody ResponseWrapper<Boolean> isLastReplica(@PathVariable String collectionName) {
		logger.info("ABOUT TO COUNT REPLICAS, FOR: " + collectionName);
		Long count = replicaService.countReplicas(collectionName);
		logger.info("NUMBER OF REPLICAS: " + count + ", FOR: " + collectionName);
		boolean result = false;
		
		if (count == SINGLE_REPLICA) {
		    result = true;	
		}
		
		return new ResponseWrapper<Boolean>(result);
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SPHINX_LOG, method = RequestMethod.POST)
	public @ResponseBody byte[] getSphinxLog(@PathVariable String collectionName, @PathVariable Long replicaNumber, @PathVariable Long recordNumber) {
		MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        return monitoringService.getSphinxLog(collectionName, replicaNumber, recordNumber);
	}

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.AVAILABLE_REPLICAS_DATA, method = RequestMethod.GET)
    public @ResponseBody ListDataViewWrapper<List<ReplicaWrapper>> getAvailableReplicas(@PathVariable @ExistsInDB(entityClass = Collection.class, fieldName = "name") String collectionName) {

        ListDataViewWrapper<List<ReplicaWrapper>> result = getReplicasData(collectionName);
        List<ReplicaWrapper> resultList = result.getList();
        for(ReplicaWrapper replicaWrapper : resultList){
            if(!replicaWrapper.getSearchServerStatus()){
                resultList.remove(replicaWrapper);
            }
        }

        return result;
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SPHINXQL_QUERY_RESULT, method = RequestMethod.POST)
    public @ResponseBody
    SphinxQLMultiResult getSphinxQlMultyQueryResults(@PathVariable @ExistsInDB(entityClass = Server.class, fieldName = "name") String serverName,
                                                     @PathVariable Integer searchdPort,
                                                     @RequestBody RequestWrapper<String> query) {
        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        SphinxQLMultiResult result = monitoringService.getSphinxQLConsoleResult(serverName, searchdPort, query.getParameter());

        return result;
    }

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.DISTRIBUTED_COLLECTION_EXTENDED_INFORMATION, method = RequestMethod.GET)
	public @ResponseBody List<SimpleCollectionWrapper> getDistributedCollectionExtendedInformation(@PathVariable String collectionName) {
		List<SimpleCollectionWrapper> wrappers = new ArrayList<SimpleCollectionWrapper>();
		Collection distributedCollection = collectionService.getCollection(collectionName);
		
		if (distributedCollection == null) {
			throw new ApplicationException("collection has not been found: " + collectionName);
		}
		
		if (distributedCollection.getCollectionType() != CollectionRoleType.DISTRIBUTED) {
			throw new ApplicationException("collection is not distributed: " + collectionName);	
		}
		
		Set<DistributedCollectionNode> nodes = distributedCollection.getDistributedCollectionNodes();
		
		for (DistributedCollectionNode node : nodes) {
			Collection simpleCollection = node.getSimpleCollection();
			List<SimpleCollectionReplicaWrapper> agentWrappers = new ArrayList<SimpleCollectionReplicaWrapper>();
			SimpleCollectionWrapper wrapper = new SimpleCollectionWrapper();
			wrapper.setCollectionName(simpleCollection.getName());
			
			
			Set<Replica> replicas = simpleCollection.getReplicas();
			
			for (Replica replica : replicas) {
				logger.info("REPLICA: " + replica.getId() + ", " + replica.getNumber());
				SimpleCollectionReplicaWrapper agentWrapper = new SimpleCollectionReplicaWrapper();
				SphinxProcess searchProcess = processService.findSearchProcess(node.getSimpleCollection().getName(), replica.getNumber());
				logger.info("SPHINX PROCESS: " + searchProcess);
				logger.info("SERVER: " + searchProcess.getServer());
				
				
				agentWrapper.setNodeHost(searchProcess.getServer().getIp());
				agentWrapper.setNodeDistribPort(searchProcess.getConfiguration().getDistributedListenPort());
				
				agentWrappers.add(agentWrapper);
				
			}
			
			wrapper.setAgents(agentWrappers);
			
			wrappers.add(wrapper);
            
		}
		
		
		return wrappers;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.DISTRIBUTED_SPHINX_CONF_PREVIEW, method = RequestMethod.POST)
	public @ResponseBody byte[] getDistributedSphinxConfPreview(@RequestBody DistributedCollectionWrapper collectionWrapper) {
		AddDistributedCollectionTask task = new AddDistributedCollectionTask();
		convertNodes(task, collectionWrapper);
		task.setType(collectionWrapper.getCollection().getType());
		logger.info("COLLECTIOn TYPE: " + task.getType());
		
		task.setReplicaNumber(FIRST_REPLICA);

        if(collectionWrapper.getSearchConfigurationPort() != null){
            task.setSearchConfigurationPort(collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
        }
        
        task.setSearchConfiguration(collectionWrapper.getSearchConfiguration());
		task.setCollection(collectionWrapper.getCollection());
		
		String content = generateDistributedSphinxConfService.generateContent(task);
		if (content != null) {

			return content.getBytes();
		}

		return null;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SIMPLE_COLLECTIONS, method = RequestMethod.GET)
	public @ResponseBody List<SimpleCollectionWrapper> getAllSimpleCollections() {
		logger.info("ABOUT TO RETRIEVE SIMPLE COLLECTIONS...");
		
		List<SimpleCollectionWrapper> wrappers = new ArrayList<SimpleCollectionWrapper>();
		
		List<Collection> simpleCollectionsWithDistributedPort = collectionService.getSimpleCollections();
		
		for (Collection collection : simpleCollectionsWithDistributedPort) {
			SimpleCollectionWrapper wrapper = new SimpleCollectionWrapper();
			
			wrapper.setCollectionName(collection.getName());
			
			List<SimpleCollectionReplicaWrapper> agentWrappers = new ArrayList<SimpleCollectionReplicaWrapper>();
			
			Set<SphinxProcess> processes = collection.getSphinxProcesses();
			
			for (SphinxProcess process : processes) {
				SimpleCollectionReplicaWrapper agentWrapper = new SimpleCollectionReplicaWrapper();
				agentWrapper.setNodeHost(process.getServer().getIp());
				ConfigurationFields field = configurationFieldsService.getDistributedPort(process.getConfiguration().getId());
				if (field != null) {
				    agentWrapper.setNodeDistribPort(new Integer(field.getFieldValue()));
				}
				
				agentWrappers.add(agentWrapper);
			}
			
			wrapper.setAgents(agentWrappers);
			wrappers.add(wrapper);
		}
		
		return wrappers;
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.DISTRIBUTED_CONFIGURATION_TEMPLATES, method = RequestMethod.GET)
	public @ResponseBody List<ConfigurationTemplate> getDistributedConfigurationTemplates() {
        List<ConfigurationTemplate> templates = configurationTemplateService.getConfigurationTemplates();
        List<ConfigurationTemplate> distributedTemplates = new ArrayList<ConfigurationTemplate>();
		
		if (templates != null) {
			for (ConfigurationTemplate template : templates) {
			    Set<ConfigurationFields> fields = template.getConfigurationFields();
			    if (fields != null) {
			        for (ConfigurationFields field : fields) {
			        	field.setConfigurationTemplate(null);
			        }
			    }
			    
			    if (template.getCollectionType() == CollectionRoleType.DISTRIBUTED) {
			    	distributedTemplates.add(template);
			    }
			}
		}
		
		return distributedTemplates;
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_SPHINX_PROCESSES, method = RequestMethod.POST)
	public @ResponseBody ListDataViewWrapper<List<SimpleCollectionReplicaWrapper>> getSearchSphinxProcessesforCollection(@PathVariable String collectionName) {
		 List<SimpleCollectionReplicaWrapper> wrappers = new ArrayList<SimpleCollectionReplicaWrapper>();
		
		 List<SphinxProcess> processes = processService.findSearchProcesses(collectionName);
		 
		 for (SphinxProcess process : processes) {
			 SimpleCollectionReplicaWrapper wrapper = new SimpleCollectionReplicaWrapper();
			 
			 wrapper.setNodeHost(process.getServer().getIp());
			 ConfigurationFields field = configurationFieldsService.getDistributedPort(process.getConfiguration().getId());
			 if (field != null) {
		         wrapper.setNodeDistribPort(new Integer(field.getFieldValue()));
			 }
			 
			 wrappers.add(wrapper);
		 }
		 
		 return new ListDataViewWrapper<List<SimpleCollectionReplicaWrapper>>(Long.valueOf(wrappers.size()), wrappers);
	}

	@Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_DISTRIBUTED_TEMPLATES, method = RequestMethod.GET)
	public @ResponseBody List<ConfigurationTemplate> getSearchDistributedConfigurationTemplates() {
		List<ConfigurationTemplate> templates = configurationTemplateService.getSearchConfigurationTemplates();
        List<ConfigurationTemplate> distributedTemplates = new ArrayList<ConfigurationTemplate>();
		
		if (templates != null) {
			for (ConfigurationTemplate template : templates) {
			    Set<ConfigurationFields> fields = template.getConfigurationFields();
			    if (fields != null) {
			        for (ConfigurationFields field : fields) {
			        	field.setConfigurationTemplate(null);
			        }
			    }
			    
			    if (template.getCollectionType() == CollectionRoleType.DISTRIBUTED) {
			    	distributedTemplates.add(template);
			    }
			}
		}
		
		return distributedTemplates;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.DISTRIBUTED_COLLECTION_WRAPPER, method = RequestMethod.GET)
	public @ResponseBody DistributedCollectionWrapper getDistributedCollectionWrapper(@PathVariable String collectionName) {
		return getDistributedCollectionWrapperByName(collectionName);
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTIONS_FOR_SNIPPET_CREATION, method = RequestMethod.GET)
	public @ResponseBody List<String> getCollectionsforSnippetCreation() {
		List<Collection> collections = collectionService.getCollectionsForSnippetCreation();
		List<String> collectionNames = new ArrayList<String>();
		if (collections != null && !collections.isEmpty()) {
			for (Collection collection : collections) {
				collectionNames.add(collection.getName());
			}
			
		}
		
		return collectionNames;
	}

	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.FIELDS_FOR_SNIPPET, method = RequestMethod.GET)
	public @ResponseBody List<String> getFieldsForSnippet(@PathVariable String collectionName) {
		Configuration configuration = configurationService.getConfiguration("index-conf-" + collectionName);
		if (configuration != null) {
			List<FieldMapping> fields = configuration.getSqlFields();
			List<String> fieldNames = new ArrayList<String>();
			
			if (fields != null && !fields.isEmpty()) {
				for (FieldMapping field : fields) {
					fieldNames.add(field.getIndexField());
				}
			}
			
			return fieldNames;
		}
		throw new ApplicationException("configuration not found for collection: " + collectionName);
	}

	/**
	 * {
      "collectionName": "collection209",
      "snippetConfiguration": {
        "id": 1,
        "preQuery": null,
        "postQuery": null,
        "mainQuery": null,
        "fullPreQuery": null,
        "fullPostQuery": null,
        "fullMainQuery": null,
        "lastBuildSnippet": null,
        "nextBuildSnippet": null,
        "fields": [
          {
            "id": 1,
            "fieldName": "1"
          }
        ]
      },
      "cron": {
        "cronSchedule": "0 * * * * ?",
        "enabled": false
      }
    }
	 */
	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SNIPPETS, method = RequestMethod.POST)
	public @ResponseBody ListDataViewWrapper<List<SnippetConfigurationViewWrapper>> getSnippets(@RequestBody SnippetSearchParameters param) {

//        return getFakeSnippetData(param);
        logger.info("ABOUT TO RETRIEVE SNIPPETS: " + param);
  		
  		if (param != null) {
  			logger.info("PAGE: " + param.getPage());
  			logger.info("START: " + param.getStart());
  			logger.info("LIMIT: " + param.getLimit());
  			logger.info("COLLECTION TERM: " + param.getCollectionName());
  		}
  		
		List<SnippetConfiguration> snippets = snippetConfigurationService.getSnippetConfigurations(param);
  		
  		  		
  		Long total = snippetConfigurationService.count(param);
  		
  		logger.info("FOUND TOTAL SNIPPETS: " + total);
  		
  		List<SnippetConfigurationViewWrapper> wrappers = new ArrayList<SnippetConfigurationViewWrapper>();
  		
  		if (snippets != null && !snippets.isEmpty()) {
  			for (SnippetConfiguration snippet : snippets) {
  				SnippetConfigurationViewWrapper item = new SnippetConfigurationViewWrapper();
  				item.setCollectionName(snippet.getCollection().getName());
  				item.setLastBuildSnippet(snippet.getLastBuildSnippet());
  				item.setNextBuildSnippet(snippet.getNextBuildSnippet());
  				
  				item.setSnippetInfoWrapper(snippetsInfoMap.get(snippet.getCollection().getName()));
  				
  				ScheduledTask scheduledTask = snippet.getScheduledTask();
  	            if(scheduledTask != null) { 
  	                item.setCronSchedule(scheduledTask.getCronSchedule());
                }
  				
  				wrappers.add(item);
  			}
  		}
  		
  		ListDataViewWrapper<List<SnippetConfigurationViewWrapper>> wrapper = new ListDataViewWrapper<List<SnippetConfigurationViewWrapper>>(total, wrappers);
  		
		return wrapper;
	}

    private ListDataViewWrapper<List<SnippetConfigurationViewWrapper>> getFakeSnippetData(SnippetSearchParameters param){

        List<SnippetConfigurationViewWrapper> snippets = new ArrayList<SnippetConfigurationViewWrapper>();
        SnippetConfigurationViewWrapper snippet;
        SnippetInfoWrapper snippetInfoWrapper = new SnippetInfoWrapper();
        Date datetmp;
        for(int i = param.getStart(); i< param.getStart() + param.getLimit(); i++){
            snippet = new SnippetConfigurationViewWrapper();
            snippet.setCollectionName("collection_"+i);
            snippet.setCronSchedule("0 0 0 0 0");
            snippet.setLastBuildSnippet(new Date());
            datetmp = new Date();
            datetmp.setTime(snippet.getLastBuildSnippet().getTime() + 1000000);
            snippet.setNextBuildSnippet(datetmp);
            snippetInfoWrapper.setIsCurrentlyFullRebuildSnippet(false);
            snippetInfoWrapper.setIsCurrentlyRebuildSnippet(false);
            snippet.setSnippetInfoWrapper(snippetInfoWrapper);
            snippets.add(snippet);
        }

        Long total = 30l;

        ListDataViewWrapper<List<SnippetConfigurationViewWrapper>> wrapper = new ListDataViewWrapper<List<SnippetConfigurationViewWrapper>>(total, snippets);

        return wrapper;
    };
	
	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.QUERY_SNIPPETS_INFO, method = RequestMethod.GET)
	public @ResponseBody ConcurrentHashMap<String, SnippetInfoWrapper> getSnippetsInfo() {
		return snippetsInfoMap;
	}
	
	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SNIPPET_CONFIGURATION_WRAPPER, method = RequestMethod.GET)
	public @ResponseBody SnippetConfigurationWrapper getSnippetConfigurationWrapper(@PathVariable String collectionName) {
		return getSnippetConfigurationWrapperByName(collectionName);
	}
	
	@Override
	@RequestMapping(value = CoordinatorViewRestURIConstants.SNIPPET_QUERY_FIELDS_INFO, method = RequestMethod.POST)
	public @ResponseBody SnippetQueryFieldsWrapper getSnippetQueryFields(@RequestBody RequestWrapper<String> sql) {
		List<String> fields = sqlParseService.getSelectFields(sql.getParameter());
		
		if (fields == null || fields.isEmpty() || fields.size() < SNIPPET_QUERY_FIELDS_MIN_COUNT) {
			throw new ApplicationException("incorrect SQL jas been provided, id field and snippet fields should be specified");
		}
		
		SnippetQueryFieldsWrapper wrapper = new SnippetQueryFieldsWrapper();
		
		wrapper.setIdName(fields.get(0));
		
		wrapper.setSnippetConfigurationFields(fields.subList(1, fields.size()));
		
		return wrapper;
		
	}

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERIES_RESULTS_GROUPED, method = RequestMethod.POST)
    public @ResponseBody ListDataViewWrapper<List<SearchQueryGrouped>> getSearchQueriesResultsGrouped(@RequestBody SearchQuerySearchParameters searchParameters) {

        List<SearchQueryGrouped> result = searchQueryProcessService.process(searchParameters);
        Long count = searchQueryProcessService.getSearchQueryGroupedCount(searchParameters);

        return new ListDataViewWrapper<List<SearchQueryGrouped>>(count, result);
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.COLLECTION_NAMES, method = RequestMethod.GET)
    public @ResponseBody List<CollectionNameWrapper> getCollectionNames(){
        List<CollectionNameWrapper> collectionNames = new ArrayList<CollectionNameWrapper>();
        List<Collection> collections = collectionService.getCollections();

        for(Collection collection : collections){
            collectionNames.add(new CollectionNameWrapper(collection.getName()));
        }

        return collectionNames;
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_TOTAL_TIME, method = RequestMethod.POST)
    public @ResponseBody List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult) {

        List<SearchQueryHistoryPoint> result = searchQueryProcessService.getQueryHistoryTotalTime(searchParameters);

        return result;
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_RESULT_COUNT, method = RequestMethod.POST)
    public @ResponseBody List<SearchQueryHistoryPoint> getQueryHistoryResultCount(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult) {

        List<SearchQueryHistoryPoint> result = searchQueryProcessService.getQueryHistoryResultCount(searchParameters);

        return result;
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_QUERY_COUNT, method = RequestMethod.POST)
    public @ResponseBody List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult) {

        List<SearchQueryHistoryPoint> result = searchQueryProcessService.getQueryHistoryQueryCount(searchParameters);

        return result;
    }

    @Override
    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_OFFSET_NOT_ZERO_COUNT, method = RequestMethod.POST)
    public @ResponseBody List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult) {

        List<SearchQueryHistoryPoint> result = searchQueryProcessService.getQueryHistoryOffsetNotZeroCount(searchParameters);

        return result;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.DATE_DETAILINGS, method = RequestMethod.GET)
    public @ResponseBody List<ValueTitle> getDateDetailings() {
        List<ValueTitle> result = new ArrayList<ValueTitle>();
        for(DateDetailing dateDetailing : DateDetailing.values()){
            result.add(new ValueTitle(dateDetailing.toString(), dateDetailing.getTitle()));
        }
        return result;
    }

    @Override
    @RequestMapping(value = CoordinatorViewRestURIConstants.DISTRIBUTED_COLLECTION_NAMES, method = RequestMethod.GET)
    public @ResponseBody List<CollectionNameWrapper> getDistributedCollectionNames(@PathVariable String collectionName){
        List<CollectionNameWrapper> collectionNames = new ArrayList<CollectionNameWrapper>();
        List<Collection> collections = collectionService.getDistributedCollections(collectionName);

        for(Collection collection : collections){
            collectionNames.add(new CollectionNameWrapper(collection.getName()));
        }

        return collectionNames;
    }
}
