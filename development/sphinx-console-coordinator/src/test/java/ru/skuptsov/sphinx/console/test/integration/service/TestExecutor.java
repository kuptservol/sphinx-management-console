package ru.skuptsov.sphinx.console.test.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibatis.common.jdbc.ScriptRunner;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.admin.model.CommandResult;
import ru.skuptsov.sphinx.console.admin.service.api.ChangesetService;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryGrouped;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistoryPoint;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistorySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuerySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;
import ru.skuptsov.sphinx.console.coordinator.task.MakeCollectionFullRebuildIndexTask;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesRestoreFailureTask;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.test.integration.tests.snippet.MakeSnippetFullRebuildTest;

import java.io.*;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * Класс - помощник для выполнения тестов
 * Created by SKuptsov on 03.01.15.
 */
@Component
public class TestExecutor {
	
	protected static Logger logger = LoggerFactory.getLogger(TestExecutor.class);

    public static final String DELETE_TEMPLATE = "delete from {0} where {1}=\''{2}\''";
    public static final String SET_COLLECTION_FAILURE_STATE_TEMPLATE = "update COLLECTION set is_processing_failed=1 where collection_name=\''{0}\''";
    public static final String INSERT_DATA_TEMPLATE = "insert into {0} ({1}) values (\''{2}\'')";
    public static final String UPDATE_DATA_TEMPLATE = "update {0} set {1} = \''{2}\'' where {3} = \''{4}\'';";
    public static final String SELECT_CREATE_DATE_TEMPLATE = "select EXTRACT(EPOCH FROM create_date AT TIME ZONE \''UTC\'') * 1000 as create_date_timestamp from {0} where {1} = ''{2}'';";
    public static final String SEARCH_TEMPLATE = "SELECT * FROM {0} where match(\''@{1} {2}\'') limit 0,0;";
    public static final String SEARCH_CONDITION_TEMPLATE = "SELECT * FROM {0} where {1}={2}) limit 0,0;";
    public static final String SEARCH_OBJECT_CONDITION_TEMPLATE = "SELECT {0} FROM {1} where {2}={3};";
    public static final String SELECT_MERGE_DATE_TEMPLATE = "select count(*) as row_count from test.merge_date where EXTRACT(EPOCH FROM merge_timestamp AT TIME ZONE \''UTC\'')::bigint = {0}";
    public static final String SPHINX_QL_QUERY_TEMPLATE = "select * from {0}";
    public static final String CONNECTION_TEMPLATE = "jdbc:mysql://{0}:{1}";
    public static final String sphinx.console_SPHINX_DIR = "/opt/sphinx.console/sphinx/";
    public static final String sphinx.console_SPHINX_SNIPPET_DIR = sphinx.console_SPHINX_DIR + "snippet/";
    public static final String sphinx.console_INDEXING_DIR_NAME = "indexing";
    public static final String sphinx.console_SEARCHING_DIR_NAME = "searching";
    public static final String FILE_TEXT_BY_FILE_NAME_TEMPLATE = "echo $(head \"$(find {0} -name {1})\")";

    @Autowired
    public ServiceUtils serviceUtils;
    
    @Autowired
    public ChangesetService changesetRepository;

    @Autowired
    public CommandLineService commandLineService;

    @Autowired
    public FileService fileService;

    @Autowired
    public TaskService taskService;

    @Autowired
    public ConverterService converterService;

    @Value("${status.retry.delay}")
    public int statusRetryDelay;

    @Value("${status.retry.max.delay}")
    public int statusRetryMaxDelay;

    public void createReplicaSimpleCollectionDistributedCollection(String distributedCollectionName, String collectionName,
                                                                   Server server, Integer port, Integer distributedPort,
                                                                   String jdbcUrl, String jdbcUsername,
                                                                   String jdbcPassword, Boolean expectedValue) throws Throwable {
        createReplica(collectionName, server, port, distributedPort);
        checkNeedReloadCollection(distributedCollectionName, jdbcUrl, jdbcUsername, jdbcPassword, expectedValue);
    }

    public void createSimpleCollectionReplicaInDistributedCollection(String changesetPath,
                                                                     String distributedCollectionName, String collectionName,
                                                                     Server server, Integer port,
                                                                     String jdbcUrl, String jdbcUsername,
                                                                     String jdbcPassword, Boolean expectedValue) throws Throwable {
        createReplica(changesetPath, collectionName, server, port);
        checkNeedReloadCollection(distributedCollectionName, jdbcUrl, jdbcUsername, jdbcPassword, expectedValue);
    }

    public void createDistributedReplica(ReplicaWrapper replicaWrapper) {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.CREATE_DISTRIBUTED_REPLICA, replicaWrapper, Status.class);
        checkStatus(status.getTaskUID());
    }

    public void addAdminProcess(Server server, int port, ProcessType processType) {
        Server uiServer = new Server();
        uiServer.setName(server.getName());
        uiServer.setIp(server.getIp());
//        uiServer.setDomain(server.getDomain());
        AdminProcess adminProcess = new AdminProcess();
        adminProcess.setType(processType);
        adminProcess.setPort(port);
        adminProcess.setServer(uiServer);
        ResponseEntity<Status> responseEntity = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.ADD_ADMIN_PROCESS, adminProcess, Status.class);
        logger.info("Responce: " + responseEntity.getBody());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
    }

    public void clearAgentFiles(String indexingAgentServerIp, int sshPort, String username, String password) {
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /opt/sphinx.console/sphinx/binlog");
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /opt/sphinx.console/sphinx/cfg");
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /opt/sphinx.console/sphinx/data");
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /opt/sphinx.console/sphinx/log");
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /opt/sphinx.console/sphinx/pid");
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /opt/sphinx.console/sphinx/snippet");
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "rm -rf /etc/init.d/sphinx.console-service_*;");
    }

    public void killallSearchdProcesses(String indexingAgentServerIp, int sshPort, String username, String password) {
        runSshCommand(indexingAgentServerIp, sshPort, username, password, "killall searchd");
    }

    public String runSshCommand(String serverIp, Integer port, String username, String password, String command) {
        logger.info("ABOUT TO RUN COMMAND: " + command);
        boolean hasException = false;
        String message = "";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, serverIp, port);
            logger.info("SSH SESSION: " + session);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            logger.info("SSH CHANNEL: " + channel);
            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.setCommand(command);
            channel.connect();

            String msg;
            while ((msg = in.readLine()) != null) {
                message += msg;
                logger.info(msg);
            }

            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            hasException = true;
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            hasException = true;
            logger.error(e.getMessage(), e);
        } finally {
            Assert.assertEquals(false, hasException);
        }

        return message;
    }

    public void breakCollection(String collectionName, String jdbcUrl, String jdbcUsername, String jdbcPassword) throws Throwable {
        setCollectionFailureState(jdbcUrl, jdbcUsername, jdbcPassword, collectionName);
    }

    private void checkRepairCollection(String taskUID, String collectionName, String searchServerIp, Integer searchServerPort) throws Throwable {
        logger.info("UID: " + taskUID);
        //check task COMPLETE
        checkStatus(taskUID);

        checkCollectionSize(collectionName, searchServerIp, searchServerPort);
    }

    public void repairCollection(CollectionWrapper collectionWrapper, String jdbcUrl, String jdbcUsername, String jdbcPassword) throws Throwable {
        String collectionName = collectionWrapper.getCollection().getName();

        breakCollection(collectionName, jdbcUrl, jdbcUsername, jdbcPassword);

        //modify collection then start it again and trying to repair
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_COLLECTION_ATTRIBUTES, collectionWrapper, Status.class);
        Assert.assertEquals(status.getCode(), 0);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(collectionWrapper);

        logger.info("UID: " + status.getTaskUID());
        checkTaskName(status.getTaskUID(), ModifyCollectionAttributesRestoreFailureTask.TASK_NAME);
        //check task COMPLETE
        checkStatus(status.getTaskUID());

        checkCollectionSize(collectionName, collectionWrapper.getSearchServer().getIp(), collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
    }

    public void repairCollection(String changesetPath,
                                 String collectionName,
                                 String jdbcUrl,
                                 String jdbcUsername,
                                 String jdbcPassword,
                                 String searchServerIp,
                                 Integer searchServerPort) throws Throwable {
        breakCollection(collectionName, jdbcUrl, jdbcUsername, jdbcPassword);

        //modify collection then start it again and trying to repair
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkRepairCollection(lastCommandResult.getTaskUid(),
                collectionName,
                searchServerIp,
                searchServerPort);
    }

    public void repairDeltaMainCollectionDistributedServerRepair(String changesetPath,
                                                                 String collectionName,
                                                                 String searchServerIp,
                                                                 Integer searchServerPort,
                                                                 TaskName expectedTaskName) throws Throwable {
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkModifyCollectionAttributesNoChange(lastCommandResult.getTaskUid(),
                                                collectionName,
                                                searchServerIp,
                                                searchServerPort,
                                                expectedTaskName);
    }


    public void repairDeltaMainCollectionDistributedServerRepair(CollectionWrapper collectionWrapper, String value) throws Throwable {
        ResponseEntity<Status> responseEntity = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.ADD_COLLECTION, collectionWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(collectionWrapper);

        logger.debug("TASK UID: " + responseEntity.getBody().getTaskUID());
        checkStatus(responseEntity.getBody().getTaskUID(), null, TaskStatus.FAILURE);

        CollectionWrapper collectionWrapper1 = getCollectionWrapper(collectionWrapper.getCollection().getName());

        //SEARCH CONF
        for(FieldMapping configurationFields : collectionWrapper1.getSearchConfiguration().getFieldMappings()) {
            configurationFields.setId(null);
        }

        for(ConfigurationFields configurationFields : collectionWrapper1.getSearchConfiguration().getSourceConfigurationFields()) {
            configurationFields.setId(null);
            if(configurationFields.getFieldKey().equals("sql_query_range")) {
                configurationFields.setFieldValue(value);
            }
        }

        //INDEX CONF
        for(FieldMapping configurationFields : collectionWrapper1.getIndexConfiguration().getFieldMappings()) {
            configurationFields.setId(null);
        }

        for(ConfigurationFields configurationFields : collectionWrapper1.getIndexConfiguration().getSourceConfigurationFields()) {
            configurationFields.setId(null);
            if(configurationFields.getFieldKey().equals("sql_query_range")) {
                configurationFields.setFieldValue(value);
            }
        }
        logger.debug("Restore old sql_query_range: " + value);
        modifyCollectionAttributesNoChange(collectionWrapper1, ModifyCollectionAttributesRestoreFailureTask.TASK_NAME);
    }


    public void checkAddCollectionResult(String collectionName, String searchServerIp, Integer searchServerPort, CollectionType type, Object... params) throws Throwable {
        ActivityLogSearchParameters activityLogSearchParameters = new ActivityLogSearchParameters();
        activityLogSearchParameters.setCollectionName(collectionName);
        activityLogSearchParameters.setPageSize(1);

        ResponseEntity<ListDataViewWrapper> tasksResponse =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASKS, activityLogSearchParameters, ListDataViewWrapper.class);

        ListDataViewWrapper<List<TaskDataViewWrapper>> tasks = (ListDataViewWrapper<List<TaskDataViewWrapper>>)tasksResponse.getBody();

        Assert.assertEquals(1, tasks.getList().size());

        Map taskDataViewWrapper = (Map)tasks.getList().get(0);

        Assert.assertEquals("addCollection", taskDataViewWrapper.get("taskName"));

        TaskLogsSearchParameters taskLogsSearchParameters = new TaskLogsSearchParameters();
        taskLogsSearchParameters.setTaskUid((String)taskDataViewWrapper.get("taskUid"));

        //Отслеживание процесса создания по логам
        String status = "";
        while (true) {

            boolean hasFinished = false;

            ResponseEntity<ListDataViewWrapper> tasksLogResponse = null;
            try {
                tasksLogResponse =
                        serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.writeValueAsString(taskLogsSearchParameters);
            } catch (Exception exception) {
                logger.error("RMI CoordinatorViewRestURIConstants.TASK_LOG call error", exception);
            }

            ListDataViewWrapper<List<TaskDataViewWrapper>> taskLogs = null;
            if(tasksLogResponse != null) {
                taskLogs = (ListDataViewWrapper<List<TaskDataViewWrapper>>)tasksLogResponse.getBody();
            }

            if(taskLogs != null) {
                for (int i = 0; i < taskLogs.getList().size(); i++) {

                    Object taskLogDataViewWrapper = taskLogs.getList().get(i);
                    Map taskLogDataViewWrapperMap = (Map)taskLogDataViewWrapper;

                    Assert.assertNotNull(taskLogDataViewWrapperMap.get("serverName"));

                    Assert.assertNotNull(taskLogDataViewWrapperMap.get("startTime"));

                    if(taskLogDataViewWrapperMap.get("operationType").equals("COMPLETED") &&
                        taskLogDataViewWrapperMap.get("status").equals("SUCCESS") ||
                            taskLogDataViewWrapperMap.get("status").equals("FAILURE")) {

                        hasFinished = true;
                        status = (String)taskLogDataViewWrapperMap.get("status");
                        logger.info("STATUS: " + status);

                        if (status.equals("SUCCESS")) {
                            //Проверка, что можно сделать запрос
                            String connectionString = "jdbc:mysql://" + searchServerIp + ":" + searchServerPort;
                            logger.info("ConnectionString: " + connectionString);
                            createJdbcTemplate("com.mysql.jdbc.Driver", connectionString, "", "")
                                    .query("select * from " + collectionName + " limit 0,0;show meta;", new RowMapper<String>() {
                                        @Override
                                        public String mapRow(ResultSet resultSet, int i) throws SQLException {
                                            String total = resultSet.getString("Value");
                                            return total;
                                        }
                                    });

                            if (type == CollectionType.MAIN_DELTA) {
                                checkIndexTables(collectionName, searchServerIp, searchServerPort); // проверка наличия созданных таблиц для индексов
                            }
                            checkCollectionSize(collectionName, searchServerIp, searchServerPort); // проверка размера индекса

                            if (type == CollectionType.MAIN_DELTA) {
                                checkExpectedValueinIndex(collectionName, searchServerIp, searchServerPort, params);    // Проверяется, что для записи с id=2 из распределённого индекса возвращается значение "Текст2"
                                //  checkExpectedValueinIndex(collectionName, indexServer.getIp(), collectionPort, params);
                            }
                        }
                    }
                }
            }

            if (hasFinished == true) {
                logger.info("FINISH STATUS: " + status);
                Assert.assertEquals("SUCCESS", status);
                return;
            }

            Thread.currentThread().sleep(3000);
        }
    }
    
    public void addDistributedCollection(DistributedCollectionWrapper collectionWrapper, CollectionType type, Object... params) throws Throwable {
    	ResponseEntity<Status> responseEntity =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.ADD_DISTRIBUTED_COLLECTION, collectionWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(collectionWrapper);

        // Старт проверок на корректность создания
        logger.info("" + responseEntity.getBody());
        Assert.assertEquals(0, responseEntity.getBody().getCode());

      /*  checkAddCollectionResult(collectionWrapper.getCollection().getName(),
                collectionWrapper.getSearchServer().getIp(),
                collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort(),
                type,
                params);*/
    }
    
    public void modifyDistributedCollection(DistributedCollectionWrapper collectionWrapper, CollectionType type, Object... params) throws Throwable {
    	ResponseEntity<Status> responseEntity =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES, collectionWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(collectionWrapper);

        // Старт проверок на корректность создания
        logger.info("" + responseEntity.getBody());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
    }

    public void addCollection(CollectionWrapper collectionWrapper, CollectionType type, Object... params) throws Throwable {
    	ResponseEntity<Status> responseEntity =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.ADD_COLLECTION, collectionWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(collectionWrapper);

        // Старт проверок на корректность создания
        logger.info("" + responseEntity.getBody());
        Assert.assertEquals(0, responseEntity.getBody().getCode());

        checkAddCollectionResult(collectionWrapper.getCollection().getName(),
                collectionWrapper.getSearchServer().getIp(),
                collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort(),
                type,
                params);
    }

    public void addCollection(String changesetPath, String collectionName, String searchServerIp, Integer searchServerPort, CollectionType type, Object... params) throws Throwable {
        changesetRepository.execute(changesetPath, false);

        checkAddCollectionResult(collectionName,
                searchServerIp,
                searchServerPort,
                type,
                params);
    }

    public void checkValidationFailure(Status status, Object... failureParams){
        logger.debug(status.toString());
        Assert.assertEquals(Status.StatusCode.REQUEST_PARAM_VALIDATION_FAILED.getCode(), status.getCode());
        Assert.assertNull(status.getTaskUID());
        for(Object failureParam : failureParams){
            Assert.assertTrue(status.getMessage().contains(failureParam.toString()));
        }
    }

    public void checkIndexTables(String collectionName, String ip, int collectionPort) {
    	List<String> expectedTables = new ArrayList<String>();
    	expectedTables.add(collectionName);
    	expectedTables.add(collectionName + "_delta");
    	expectedTables.add(collectionName + "_main");
    	
    	List<String> actualTables =
                createJdbcTemplate("com.mysql.jdbc.Driver", "jdbc:mysql://" + ip +":" + collectionPort, "", "").
                        query("show tables", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                                String indexTable = resultSet.getString("Index");
                                logger.info("INDEX TABLE: " + indexTable);
                                return indexTable;
                                }
                            });
    	
    	Assert.assertTrue(CollectionUtils.isEqualCollection(expectedTables, actualTables));
    }
    
    public void checkCollectionSize(String collectionName, String ip, int collectionPort) throws Throwable {
    	Long expectedSize = getCollectionSize(collectionName, ip, collectionPort);
    	logger.info("EXPECTED COLLECTION SIZE: " + expectedSize);
    	Long actualSize = getCollectionSizeFromRest(collectionName);
    	Assert.assertEquals(expectedSize, actualSize);
    }
    
    public void checkExpectedValueinIndex(String collectionName, String ip, int collectionPort, Object... params) {
    	logger.info("CHECK EXPECTED VALUE IN INDEX");
    	Long deltaMainIdToFindValue = (Long)params[0];
    	String deltaMainExpectedValue = (String)params[1];
    	final String deltaMainColumnValue = (String)params[2];
        logger.info("ID TO FIND VALUE: " + deltaMainIdToFindValue);
        logger.info("EXPECTED VALUE: " + deltaMainExpectedValue);
        logger.info("COLUMN TO FIND VALUE: " + deltaMainColumnValue);
    	
    	String actualValue = createJdbcTemplate("com.mysql.jdbc.Driver", "jdbc:mysql://" + ip +":" + collectionPort + "/?useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true", "", "")
        .queryForObject("select * from " + collectionName + " where id = ?", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                String actualValue = resultSet.getString(deltaMainColumnValue);
                logger.info("VALUE: " + actualValue);
                return actualValue;
            }
        }, deltaMainIdToFindValue);
    	logger.info("ACTUAL VALUE FROM INDEX: " + actualValue);
    	Assert.assertTrue(actualValue.contains(deltaMainExpectedValue));
    }
    
    public Long getCollectionSizeFromRest(String collectionName) {
    	Long size = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.COLLECTION_SIZE, Long.class, collectionName);	
    	logger.info("COLLECTION SIZE FROM REST: " + size);

        return size;
    }

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * Фул-ребилд еще не выполнялся, поэтому статус должен быть NOT_RUNNING*/
    public void fullIndexingStateNotRunning(String collectionName) {
        Assert.assertTrue(getCollectionInfo(collectionName).getFullIndexingResult().getFullIndexingState() == FullIndexingState.NOT_RUNNING);
    }

    public CollectionInfoWrapper getCollectionInfo(String collectionName) {
        Map map = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.QUERY_COLLECTIONS_INFO_FROM_AGENT, Map.class);
        Map<String, String> statuses = (Map<String, String>)((Map) map.get(collectionName)).get("processStatuses");
        Map<Long, ProcessStatus> processStatuses = new HashMap<Long, ProcessStatus>();
        for(Map.Entry<String, String> entry : statuses.entrySet()) {
            processStatuses.put(Long.valueOf(entry.getKey()), ProcessStatus.valueOf(entry.getValue()));
        }
        ((Map) map.get(collectionName)).remove("processStatuses");
        ((Map) map.get(collectionName)).remove("allProcessStatus");
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionInfoWrapper collectionInfoWrapper = objectMapper.convertValue(map.get(collectionName), CollectionInfoWrapper.class);
        collectionInfoWrapper.setProcessStatuses(processStatuses);

        return collectionInfoWrapper;
    }

    public Long getCollectionSize(String collectionName, String ip, int collectionPort) throws Throwable {
        String query = "SELECT * FROM " + collectionName + " limit 0,0;";
        return getRowCountForCollectionQuery(query, ip, collectionPort);
    }

    public void checkSearchProcessAvailable(String ip, int port){
        Assert.assertTrue(searchProcessAvailable(ip, port));
    }

    public void checkSearchProcessUnavailable(String ip, int port){
        Assert.assertFalse(searchProcessAvailable(ip, port));
    }

    public boolean searchProcessAvailable(String ip, int port){
        String connectionString = MessageFormat.format(CONNECTION_TEMPLATE, ip, port);
        logger.info("Check connection for: " + connectionString);

        try {
            createJdbcTemplate("com.mysql.jdbc.Driver", connectionString, "", "")
                    .execute("show tables;");
            return true;
        } catch (Throwable e) {
            logger.info(MessageFormat.format("Search process {0}:{1} not available", ip, port));
            return false;
        }
    }

    public Long getRowCountForCollectionQuery(String query, String ip, int collectionPort) throws Throwable {
        String connectionString = "jdbc:mysql://" + ip +":" + collectionPort + "?autoReconnect=true"; //+ "?useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true&maxAllowedPacket=512000";
        logger.info(connectionString);

        org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = new DriverManagerDataSource();//ApplicationContextProvider.getBean("monitoringDataSource");

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(connectionString);
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = dataSource.getConnection();
            stmt = connection.createStatement();

            if (stmt.execute(query)) {
                do {
                    ResultSet rs = stmt.getResultSet();
                    rs.first();
                } while (stmt.getMoreResults());
            }

            ResultSet rs = stmt.executeQuery("SHOW META;");
            while (rs.next()) {
                try {
                    logger.info("META INFO: " + rs.getString("Variable_name") + ' ' + rs.getString("Value"));

                    if (rs.getString("Variable_name") != null && rs.getString("Variable_name").equals("total_found")) {
                        return rs.getLong("Value");
                    }

                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                    throw e;
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }

        return 0L;
    }

    public void checkIsIndexingDeltaFalse(String collectionName){
        Assert.assertFalse(getCollectionInfo(collectionName).getIsCurrentlyIndexingDelta());
    }

    public void checkIsIndexingDeltaTrue(String collectionName){
        Assert.assertTrue(getCollectionInfo(collectionName).getIsCurrentlyIndexingDelta());
    }

    public void addSearchDataIntoDBTable(String host, Integer port, String dbName, String userName, String userPassword, String tableName, String textColumnName, String text) {
        String insertDataForPlainCollectionQuery = MessageFormat.format(INSERT_DATA_TEMPLATE, tableName, textColumnName, text);

        executeQuery(DataSourceType.PGSQL, host, port, dbName, userName, userPassword, insertDataForPlainCollectionQuery);
    }

    public void updateSearchDataInDBTable(String host, Integer port, String dbName, String userName, String userPassword, String tableName, String textColumnName, String text, String newText) {
        String updateQuery = MessageFormat.format(UPDATE_DATA_TEMPLATE, tableName, textColumnName, newText, textColumnName, text);

        executeQuery(DataSourceType.PGSQL, host, port, dbName, userName, userPassword, updateQuery);
    }

    public Long selectCreateDateFromDBTable(String host, Integer port, String dbName, String userName, String userPassword, String tableName, String textColumnName, String text) {
        String sql = MessageFormat.format(SELECT_CREATE_DATE_TEMPLATE, tableName, textColumnName, text);

        Long createDateTimestamp = createJdbcTemplate(DataSourceType.PGSQL.getDriverClass(), "jdbc:postgresql://" + host +":" + port + "/?useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true", userName, userPassword)
                .queryForObject(sql, new RowMapper<Long>() {
                    @Override
                    public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getLong("create_date_timestamp");
                    }
                });

        return createDateTimestamp;
    }

    public Object selectObjectFromDBTable(String host, Integer port, String userName, String userPassword, String tableName, final String objectColumnName, String condColumnName, String condColumnValue) {
        String sql = MessageFormat.format(SEARCH_OBJECT_CONDITION_TEMPLATE, objectColumnName, tableName, condColumnName, condColumnValue);
        logger.info("Execute query: " + sql);

        Object result = createJdbcTemplate(DataSourceType.PGSQL.getDriverClass(), "jdbc:postgresql://" + host +":" + port + "/?useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true", userName, userPassword)
                .queryForObject(sql, new RowMapper<Object>() {
                    @Override
                    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getObject(objectColumnName);
                    }
                });

        logger.info("Result: " + result);
        return result;
    }

    public void checkMergeDate(String host, Integer port, String dbName, String userName, String userPassword, Long createDateTimestamp) {
        String sql = MessageFormat.format(SELECT_MERGE_DATE_TEMPLATE, Long.toString(createDateTimestamp));
        logger.info("Select merge date: " + sql);
        Integer count = createJdbcTemplate(DataSourceType.PGSQL.getDriverClass(), "jdbc:postgresql://" + host +":" + port + "/?useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true", userName, userPassword)
                .queryForObject(sql, new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getInt("row_count");
                    }
                });

        logger.info("Count: " + count);
        Assert.assertTrue(count == 1);
    }

    public void deleteSearchDataFromDBTable(String host, Integer port, String dbName, String userName, String userPassword, String tableName, String idColumnName, String textColumnName, String text) {
        String deleteDataForPlainCollectionQuery = MessageFormat.format(DELETE_TEMPLATE, tableName, textColumnName, text);

        executeQuery(DataSourceType.PGSQL, host, port, dbName, userName, userPassword, deleteDataForPlainCollectionQuery);
    }

    public void setCollectionFailureState(String url, String userName, String userPassword, String collectionName) {
        String setCollectionFailureStateTemplateQuery = MessageFormat.format(SET_COLLECTION_FAILURE_STATE_TEMPLATE, collectionName);

        executeQuery(DataSourceType.MYSQL, url, userName, userPassword, setCollectionFailureStateTemplateQuery);
    }

    public void executeQuery(DataSourceType dataSourceType, String host, Integer port, String dbName, String userName, String userPassword, String query) {
        String url = dataSourceType.getUrl(host, port, dbName);
        logger.info("Execute query, url: " + url);
        logger.info("Query: " + query);
        executeQuery(dataSourceType, url, userName, userPassword, query);
    }

    public void executeQuery(DataSourceType dataSourceType, String url, String userName, String userPassword, String query) {
        org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = new DriverManagerDataSource();//ApplicationContextProvider.getBean("monitoringDataSource");

        dataSource.setDriverClassName(dataSourceType.getDriverClass());
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(userPassword);
        Properties connProps = new Properties();
        connProps.setProperty("autoReconnect", "true");
        dataSource.setConnectionProperties(connProps);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();

            stmt.execute(query);
        } catch (Throwable e) {
            logger.error("Error during sql statement execution", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public void checkTaskName(String taskUid, TaskName expectedTaskName){
        //Отслеживание процесса создания по логам
        TaskLogsSearchParameters taskLogsSearchParameters = new TaskLogsSearchParameters();
        taskLogsSearchParameters.setTaskUid(taskUid);
        taskLogsSearchParameters.setLast(true);

        ResponseEntity<ListDataViewWrapper> tasksLogResponse = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
        ObjectMapper objectMapper = new ObjectMapper();
        TaskDataViewWrapper taskDataViewWrapper = objectMapper.convertValue(((ArrayList)tasksLogResponse.getBody().getList()).get(0), TaskDataViewWrapper.class);
        Assert.assertEquals(taskDataViewWrapper.getTaskName(), expectedTaskName.getTitle());
    }

    public void checkStatus(String taskUid) {
        checkStatus(taskUid, "COMPLETED", TaskStatus.SUCCESS);
    }

    public void checkStatus(String taskUid, String operationType, TaskStatus expectedTaskStatus) {
        int delay = statusRetryDelay;
        int maxDelay = statusRetryMaxDelay;
        //Отслеживание процесса создания по логам
        TaskLogsSearchParameters taskLogsSearchParameters = new TaskLogsSearchParameters();
        taskLogsSearchParameters.setTaskUid(taskUid);

        boolean failure = false;

        while(true) {
            ResponseEntity<ListDataViewWrapper> tasksLogResponse = null;
            try {
                tasksLogResponse = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
            } catch(Exception exception) {
                logger.error("RMI CoordinatorViewRestURIConstants.TASK_LOG call error", exception);
            }

            ListDataViewWrapper<List> taskLogs = null;
            if(tasksLogResponse != null) {
                taskLogs = tasksLogResponse.getBody();
            }

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                if (taskLogs != null) {
                    for (Object map : taskLogs.getList()) {
                        TaskDataViewWrapper taskDataViewWrapper = objectMapper.convertValue(map, TaskDataViewWrapper.class);
                        TaskStatus taskStatus = taskDataViewWrapper.getStatus();
                        if (!(taskStatus.equals(TaskStatus.SUCCESS) ||
                                taskStatus.equals(TaskStatus.RUNNING))) {
                            if(failure){
                                logger.info(MessageFormat.format("Second bad task status. TaskUid: {0}. Status: {1}. Test now fails.", taskUid, taskStatus));
                                Assert.assertFalse(true);
                            }
                            logger.info(MessageFormat.format("First bad task status. TaskUid: {0}. Status: {1}. Wait for retry task...", taskUid, taskStatus));
                            failure = true;
                        }
                        if ((operationType == null ||
                                taskDataViewWrapper.getOperationType().equals(operationType)) &&
                                taskStatus.equals(expectedTaskStatus)) {
                            logger.info(MessageFormat.format("Waited for task finish for {0} seconds. Last received operation: {1}", delay / 1000, taskDataViewWrapper.getOperationType()));
                            return;
                        }
                    }
                }

                Assert.assertTrue("Didn't get success status for " + maxDelay + " ms", delay < maxDelay);
                Thread.currentThread().sleep(delay += delay);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public CollectionWrapper getCollectionWrapper(String collectionName) throws InterruptedException {
        ResponseEntity<ResponseWrapper> responseEntity = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.COLLECTION_WRAPPER, null, ResponseWrapper.class, collectionName);
        ResponseWrapper<CollectionWrapper> responseWrapper = (ResponseWrapper<CollectionWrapper>)responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionWrapper collectionWrapper = objectMapper.convertValue(responseWrapper.getResult(), CollectionWrapper.class);
//        try {
//            objectMapper.writeValueAsString(collectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        Assert.assertNotNull("Collection " + collectionName + " not found", collectionWrapper);

        return collectionWrapper;
    }

    public SnippetConfigurationWrapper getSnippetConfigurationWrapper(String collectionName) throws InterruptedException {
        return serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SNIPPET_CONFIGURATION_WRAPPER, SnippetConfigurationWrapper.class, collectionName);
    }
    
    public DistributedCollectionWrapper getDistributedCollectionWrapper(String collectionName) {
        DistributedCollectionWrapper collectionWrapper = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.DISTRIBUTED_COLLECTION_WRAPPER, DistributedCollectionWrapper.class, collectionName);
//        try {
//            objectMapper.writeValueAsString(collectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        Assert.assertNotNull("Collection " + collectionName + " not found", collectionWrapper);

        return collectionWrapper;
    }
    
    public List<SimpleCollectionReplicaWrapper> getAgents(String collectionName)  {
    	
    	ResponseEntity<ListDataViewWrapper> response =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_SPHINX_PROCESSES, null, ListDataViewWrapper.class, collectionName);

        ListDataViewWrapper<List<SimpleCollectionReplicaWrapper>> agents = (ListDataViewWrapper<List<SimpleCollectionReplicaWrapper>>)response.getBody();

    	
        return agents.getList();
    }

    public void rebuildCollection(String collectionName) throws Throwable {
        logger.info("Rebuild collection: " + collectionName);
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.REBUILD_COLLECTION, null, Status.class, collectionName);
        Assert.assertEquals(status.getCode(), 0);
        checkStatus(status.getTaskUID());
    }

    public void rebuildCollection(String changesetPath, String collectionName, String ip, int collectionPort, String fieldName, String searchText) throws Throwable {
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkStatus(lastCommandResult.getTaskUid());
        checkSearchSuccess(collectionName, ip, collectionPort, fieldName, searchText);
    }

    public void rebuildCollectionAdmin(String changesetPath) throws Throwable {
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkStatus(lastCommandResult.getTaskUid());
    }

    public void checkSearchSuccess(String collectionName, String ip, int collectionPort, String fieldName, String searchText) throws Throwable {
        String query = MessageFormat.format(SEARCH_TEMPLATE, collectionName, fieldName, searchText);
        logger.info(MessageFormat.format("Query for sphinx {0}:{1}, {2}", ip, collectionPort, query));
        Long rowCount = getRowCountForCollectionQuery(query, ip, collectionPort);
        logger.info("Row count: " + rowCount);
        Assert.assertTrue(rowCount > 0);
    }

    public void checkSearchSuccessByCondition(String collectionName, String ip, int collectionPort, String attrName, String attrValue) throws Throwable {
        String query = MessageFormat.format(SEARCH_CONDITION_TEMPLATE, collectionName, attrName, attrValue);
        logger.info(MessageFormat.format("Query for sphinx {0}:{1}, {2}", ip, collectionPort, query));
        Long rowCount = getRowCountForCollectionQuery(query, ip, collectionPort);
        logger.info("Row count: " + rowCount);
        Assert.assertTrue(rowCount > 0);
    }

    public void checkSearchFailsByCondition(String collectionName, String ip, int collectionPort, String attrName, String attrValue) throws Throwable {
        String query = MessageFormat.format(SEARCH_CONDITION_TEMPLATE, collectionName, attrName, attrValue);
        logger.info(MessageFormat.format("Query for sphinx {0}:{1}, {2}", ip, collectionPort, query));
        Long rowCount = getRowCountForCollectionQuery(query, ip, collectionPort);
        logger.info("Row count: " + rowCount);
        Assert.assertTrue(rowCount == 0);
    }

    public void checkSearchFails(String collectionName, String ip, int collectionPort, String fieldName, String searchText) throws Throwable {
        String query = MessageFormat.format(SEARCH_TEMPLATE, collectionName, fieldName, searchText);
        Long rowCount = getRowCountForCollectionQuery(query, ip, collectionPort);
        logger.info("Row count: " + rowCount);
        Assert.assertTrue(rowCount == 0);
    }

    private void checkModifyCollectionAttributesNoChange(String taskUID, String collectionName, String serverIp, Integer searchConfigurationPort, TaskName expectedTaskName) throws Throwable {
        logger.info("UID: " + taskUID);
        checkTaskName(taskUID, expectedTaskName);
        checkStatus(taskUID);

        Long collectionSize = getCollectionSize(collectionName, serverIp, searchConfigurationPort);
        Long infoCollectionSize = getCollectionInfo(collectionName).getCollectionSize();
        logger.info("Collection size: " + collectionSize + " Collection size from info: " + infoCollectionSize);
        Assert.assertEquals(collectionSize, infoCollectionSize);
    }

    public void modifyCollectionAttributesNoChange(String changesetPath,
                                                   String collectionName,
                                                   String searchServerIp,
                                                   Integer searchServerPort,
                                                   TaskName expectedTaskName) throws Throwable {
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkModifyCollectionAttributesNoChange(lastCommandResult.getTaskUid(),
                collectionName,
                searchServerIp,
                searchServerPort,
                expectedTaskName);
    }

    public void modifyCollectionAttributesNoChange(CollectionWrapper collectionWrapper, TaskName expectedTaskName) throws Throwable {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(collectionWrapper);
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_COLLECTION_ATTRIBUTES, collectionWrapper, Status.class);

        Assert.assertEquals(status.getCode(), 0);

        checkModifyCollectionAttributesNoChange(status.getTaskUID(),
                collectionWrapper.getCollection().getName(),
                collectionWrapper.getSearchServer().getIp(),
                collectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort(),
                expectedTaskName);
    }

    public void modifyCollectionAttributes(CollectionWrapper collectionWrapper, String newFieldName) throws InterruptedException, SQLException, ClassNotFoundException {
        FieldMapping fieldMappingNew = new FieldMapping();
        fieldMappingNew.setIndexField(newFieldName);
        fieldMappingNew.setSourceField(newFieldName);
        fieldMappingNew.setIndexFieldCommentary(newFieldName);
        fieldMappingNew.setIndexFieldType(IndexFieldType.SQL_FIELD_STRING);
        fieldMappingNew.setIsId(false);

        Configuration searchConfiguration = collectionWrapper.getSearchConfiguration();
        Configuration indexConfiguration = collectionWrapper.getIndexConfiguration();

        LinkedHashSet<FieldMapping> searchFieldMappings = searchConfiguration.getFieldMappings();
        LinkedHashSet<FieldMapping> indexFieldMappings = indexConfiguration.getFieldMappings();
        searchFieldMappings.add(fieldMappingNew);
        indexFieldMappings.add(fieldMappingNew);

        searchConfiguration.setFieldMappings(searchFieldMappings);
        indexConfiguration.setFieldMappings(indexFieldMappings);

        ConfigurationFields mainSqlQueryField;
        ConfigurationFields deltaSqlQueryField;
        String newFieldPatternString = ", " + newFieldName;
        String replaceBeforePattern = " from";
        Configuration[] configurations= {searchConfiguration,indexConfiguration};
        for(Configuration configuration : configurations){
            mainSqlQueryField = configuration.getMainSqlQueryField();
            mainSqlQueryField.setFieldValue(mainSqlQueryField.getFieldValue().replaceAll(newFieldPatternString,"").replace(replaceBeforePattern, ", " + newFieldName + replaceBeforePattern));
            if(collectionWrapper.getCollection().getType() == CollectionType.MAIN_DELTA){
                deltaSqlQueryField = configuration.getDeltaSqlQueryField();
                deltaSqlQueryField.setFieldValue(deltaSqlQueryField.getFieldValue().replaceAll(newFieldPatternString,"").replace(replaceBeforePattern, ", " + newFieldName + replaceBeforePattern));
            }
        }

        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_COLLECTION_ATTRIBUTES, collectionWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writeValueAsString(collectionWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        Assert.assertEquals(status.getCode(), 0);

        checkModifyCollectionAttributes(status.getTaskUID());
    }

    public void checkModifyCollectionAttributes(String taskUID) throws InterruptedException, SQLException, ClassNotFoundException {
        logger.info("UID: " + taskUID);
        checkTaskName(taskUID, MakeCollectionFullRebuildIndexTask.TASK_NAME);
        checkStatus(taskUID);
    }

    public void modifyCollectionAttributes(String changesetPath) throws InterruptedException, SQLException, ClassNotFoundException {
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkModifyCollectionAttributes(lastCommandResult.getTaskUid());
    }
    
    public DistributedCollectionWrapper buildDistributedCollectionWrapper(String collectionName, Server searchServer, final int collectionPort, String... simpleCollectionNames) {
    	 DistributedCollectionWrapper distributedCollectionWrapper = new DistributedCollectionWrapper();
    	 
    	 distributedCollectionWrapper.setSearchServer(searchServer);
    	 
    	 Collection collection = new Collection();
         collection.setType(CollectionType.DISTRIBUTED);
         collection.setCollectionType(CollectionRoleType.DISTRIBUTED);
         collection.setNeedReload(false);
         collection.setName(collectionName);
         distributedCollectionWrapper.setCollection(collection);
         
         List<SimpleCollectionWrapper> nodes = new ArrayList<SimpleCollectionWrapper>();

         for(String simpleCollectionName : simpleCollectionNames) {
             SimpleCollectionWrapper wrapper = new SimpleCollectionWrapper();
             wrapper.setCollectionName(simpleCollectionName);

             List<SimpleCollectionReplicaWrapper> agents = getAgents(simpleCollectionName);
             logger.info("AGENTS: " + agents);
             wrapper.setAgents(agents);
             nodes.add(wrapper);
         }

         distributedCollectionWrapper.setNodes(nodes);
         
         distributedCollectionWrapper.setSearchConfigurationPort(new SearchConfigurationPortWrapper() {{
             setSearchConfigurationPort(collectionPort);
         }});
         
         Configuration searchConfiguration = new Configuration();
         createDistributedConfiguration(searchConfiguration);

         distributedCollectionWrapper.setSearchConfiguration(searchConfiguration);

    	 return distributedCollectionWrapper;
    }

    public CollectionWrapper buildPlainCollectionWrapper(String cron, Server searchServer, Server indexingServer, String collectionName, int databasePort,
                                                         String dataBaseHost, String databaseDB, String dataBaseUserName, String dataBasePassword,
                                                         String testDataSourceType, String sqlQuery, String tableName, String testDataSourceTableColumnId,
                                                         String testDataSourceTableColumnField, final int collectionPort, final int distribPort, String testDataSourceTableColumnSqlField) throws InterruptedException, SQLException, ClassNotFoundException {
        CollectionWrapper collectionWrapper = new CollectionWrapper();

        DataSourceType dataSourceType = DataSourceType.getByTitle(testDataSourceType);

        if (StringUtils.isNotEmpty(tableName))
            collectionWrapper.setTableName(tableName);

        CronScheduleWrapper cronSchedule = new CronScheduleWrapper();
        cronSchedule.setCronSchedule(cron);
        collectionWrapper.setCronSchedule(cronSchedule);

        CronScheduleWrapper maincronSchedule = new CronScheduleWrapper();
        maincronSchedule.setCronSchedule(cron);
        collectionWrapper.setMainCronSchedule(maincronSchedule);

        collectionWrapper.setSearchServer(searchServer);
        collectionWrapper.setIndexServer(indexingServer);

        Collection collection = new Collection();
//        collection.setId(1L);
        collection.setType(CollectionType.SIMPLE);
        collection.setCollectionType(CollectionRoleType.SIMPLE);
        collection.setName(collectionName);
        collectionWrapper.setCollection(collection);

        Delta delta = new Delta();
        delta.setType(DeltaType.DELTA);

        collection.setDelta(delta);

        DataSource dataSource = new DataSource();
        dataSource.setName("plainCollectionDataSource");
        dataSource.setPort(databasePort);
        dataSource.setHost(dataBaseHost);
        dataSource.setSqlDb(databaseDB);
        dataSource.setUser(dataBaseUserName);
        dataSource.setPassword(dataBasePassword);
        dataSource.setType(dataSourceType);

        Configuration searchConfiguration = new Configuration();
        //searchConfiguration.setName("search-conf-"+collectionName+"-1");
        createConfiguration(searchConfiguration, dataSource, testDataSourceTableColumnId, testDataSourceTableColumnField, sqlQuery, testDataSourceTableColumnSqlField, tableName);


        collectionWrapper.setSearchConfiguration(searchConfiguration);

        Configuration indexConfiguration = new Configuration();
        //indexConfiguration.setName("index-conf-"+collectionName);
        createConfiguration(indexConfiguration, dataSource, testDataSourceTableColumnId, testDataSourceTableColumnField, sqlQuery, testDataSourceTableColumnSqlField, tableName);

        collectionWrapper.setSearchConfigurationPort(new SearchConfigurationPortWrapper() {{
            setSearchConfigurationPort(collectionPort);
        }});
        
        collectionWrapper.setDistributedConfigurationPort(new DistributedConfigurationPortWrapper() {{
            setDistributedConfigurationPort(distribPort);
        }});


        collectionWrapper.setIndexConfiguration(indexConfiguration);

        return collectionWrapper;
    }

    public ReplicaWrapper buildDistributedReplicaWrapper(String collectionName, Server searchServer, Integer searchPort) {
        ReplicaWrapper replicaWrapper = buildReplicaWrapper(collectionName, searchServer, searchPort, null);
        replicaWrapper.setCollectionType(CollectionRoleType.DISTRIBUTED);
        return replicaWrapper;
    }

    public CollectionWrapper buildDeltaMainCollectionWrapper(
            String cronMain,
            String cronDelta,
            Server searchServer,
            Server indexingServer,
            String collectionName,
            int databasePort,
            String dataBaseHost,
            String databaseDB,
            String dataBaseUserName,
            String dataBasePassword,
            String deltaMainDataSourceType,
            String mainSqlQuery,
            String deltaSqlQuery,
            String deltaMainExternalActionCode,
            String deltaMainMainSqlQueryRange,
            String deltaMainMainSqlRangeStep,
            final int collectionPort,
            String deltaMainDeltaSqlQueryPre,
            String deltaMainDeltaSqlQueryRange,
            String deltaMainDeltaSqlQueryPostIndex,
            String deltaMainDeltaSqlQueryRangeStep,
            String deleteSchemeRequest,
            Set<FieldMapping> fieldMappings, final int distribPort) throws InterruptedException, SQLException, ClassNotFoundException {
		CollectionWrapper collectionWrapper = new CollectionWrapper();
		
		DataSourceType dataSourceType = DataSourceType.getByTitle(deltaMainDataSourceType);
			
		CronScheduleWrapper cronSchedule = new CronScheduleWrapper();
		cronSchedule.setCronSchedule(cronDelta);
		collectionWrapper.setCronSchedule(cronSchedule);
		
		CronScheduleWrapper maincronSchedule = new CronScheduleWrapper();
		maincronSchedule.setCronSchedule(cronMain);
		collectionWrapper.setMainCronSchedule(maincronSchedule);
		
		collectionWrapper.setSearchServer(searchServer);
		collectionWrapper.setIndexServer(indexingServer);
		
		Collection collection = new Collection();
		collection.setType(CollectionType.MAIN_DELTA);
		collection.setName(collectionName);
		collectionWrapper.setCollection(collection);
		
		DataSource dataSource = new DataSource();
        dataSource.setName("deltaMainCollectionDataSource");
		dataSource.setPort(databasePort);
		dataSource.setHost(dataBaseHost);
		dataSource.setSqlDb(databaseDB);
		dataSource.setUser(dataBaseUserName);
		dataSource.setPassword(dataBasePassword);
		dataSource.setType(dataSourceType);
		
		Delta delta = new Delta();
		delta.setType(DeltaType.DELTA);
		ExternalAction externalAction = new ExternalAction();
		externalAction.setType(ExternalActionType.SQL);
		externalAction.setCode(deltaMainExternalActionCode);
		externalAction.setDataSource(dataSource);
		
		delta.setExternalAction(externalAction);

		collection.setDelta(delta);

		Configuration searchConfiguration = new Configuration();
		createDeltaMainConfiguration(searchConfiguration, dataSource, deltaMainMainSqlQueryRange, deltaMainMainSqlRangeStep, mainSqlQuery, deltaSqlQuery, deltaMainDeltaSqlQueryPre, deltaMainDeltaSqlQueryRange, deltaMainDeltaSqlQueryPostIndex, deltaMainDeltaSqlQueryRangeStep, fieldMappings);
		
		collectionWrapper.setSearchConfiguration(searchConfiguration);
		
		Configuration indexConfiguration = new Configuration();
		createDeltaMainConfiguration(indexConfiguration, dataSource, deltaMainMainSqlQueryRange, deltaMainMainSqlRangeStep, mainSqlQuery, deltaSqlQuery, deltaMainDeltaSqlQueryPre, deltaMainDeltaSqlQueryRange, deltaMainDeltaSqlQueryPostIndex, deltaMainDeltaSqlQueryRangeStep, fieldMappings);
		
		collectionWrapper.setSearchConfigurationPort(new SearchConfigurationPortWrapper() {{
                                                        setSearchConfigurationPort(collectionPort);
                                                    }});
		
		collectionWrapper.setDistributedConfigurationPort(new DistributedConfigurationPortWrapper() {{
            setDistributedConfigurationPort(distribPort);
        }});

		
		collectionWrapper.setIndexConfiguration(indexConfiguration);
		
		return collectionWrapper;
    }
    
    private void createDeltaMainConfiguration(Configuration configuration, DataSource dataSource,
                                              String deltaMainMainSqlQueryRange, String deltaMainMainSqlRangeStep, String mainSqlQuery,
                                              String deltaSqlQuery, String deltaMainDeltaSqlQueryPre, String deltaMainDeltaSqlQueryRange,
                                              String deltaMainDeltaSqlQueryPostIndex, String deltaMainDeltaSqlQueryRangeStep, Set<FieldMapping> fieldMappings) {
        //Configuration
        ConfigurationTemplate[] searchConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(searchConfigurationTemplates.length, 1);

        configuration.setSearchConfigurationTemplate(searchConfigurationTemplates[0]);

        ConfigurationTemplate[] indexConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.INDEXER_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(indexConfigurationTemplates.length, 1);

        configuration.setIndexerConfigurationTemplate(indexConfigurationTemplates[0]);

        ConfigurationTemplate[] configurationConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.CONFIGURATION_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertTrue(configurationConfigurationTemplates.length > 0);

        configuration.setConfigurationTemplate(configurationConfigurationTemplates[0]);


        ///DataSource and FieldMapping
        configuration.setDatasource(dataSource);

        SourceWrapper sourceWrapper = new SourceWrapper();
        sourceWrapper.setDatasource(dataSource);
        sourceWrapper.setMainSqlQuery(mainSqlQuery);

        ResponseEntity<List> result = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.QUERY_META_DATA, sourceWrapper, List.class);
        ObjectMapper objectMapper = new ObjectMapper();
        DBTableColumn column = null;
        for(Object map : result.getBody()){
            column = objectMapper.convertValue(map, DBTableColumn.class);
            for(FieldMapping fieldMapping : fieldMappings){
                if(fieldMapping.getSourceField().equals(column.getName())){
                    fieldMapping.setSourceFieldType(column.getType());
                }
            }
        }

        configuration.setFieldMappings(fieldMappings);

        Set<ConfigurationFields> sourceConfigurationFields = new HashSet<ConfigurationFields>();

        ConfigurationFields field1 = new ConfigurationFields();
        field1.setConfigurationType(ConfigurationType.SOURCE);
        field1.setFieldKey("sql_query_range");
        field1.setIndexType(IndexType.MAIN);
        field1.setFieldValue(deltaMainMainSqlQueryRange);
        sourceConfigurationFields.add(field1);


        ConfigurationFields field2 = null;
        if(StringUtils.isNotEmpty(deltaMainMainSqlRangeStep)){
            field2 = new ConfigurationFields();
            field2.setConfigurationType(ConfigurationType.SOURCE);
            field2.setFieldKey("sql_range_step");
            field2.setIndexType(IndexType.MAIN);
            field2.setFieldValue(deltaMainMainSqlRangeStep);
            sourceConfigurationFields.add(field2);
        }

        ConfigurationFields field3 = new ConfigurationFields();
        field3.setConfigurationType(ConfigurationType.SOURCE);
        field3.setFieldKey("sql_query");
        field3.setFieldValue(mainSqlQuery);
        field3.setIndexType(IndexType.MAIN);
        sourceConfigurationFields.add(field3);

        ConfigurationFields field4 = new ConfigurationFields();
        field4.setConfigurationType(ConfigurationType.SOURCE);
        field4.setFieldKey("sql_query");
        field4.setFieldValue(deltaSqlQuery);
        field4.setIndexType(IndexType.DELTA);
        sourceConfigurationFields.add(field4);

        ConfigurationFields field5 = null;
        if(StringUtils.isNotEmpty(deltaMainDeltaSqlQueryPre)){
            field5 = new ConfigurationFields();
            field5.setConfigurationType(ConfigurationType.SOURCE);
            field5.setFieldKey("sql_query_pre");
            field5.setFieldValue(deltaMainDeltaSqlQueryPre);
            field5.setIndexType(IndexType.DELTA);
            sourceConfigurationFields.add(field5);
        }

        ConfigurationFields field6 = null;
        if(StringUtils.isNotEmpty(deltaMainDeltaSqlQueryRange)){
            field6 = new ConfigurationFields();
            field6.setConfigurationType(ConfigurationType.SOURCE);
            field6.setFieldKey("sql_query_range");
            field6.setFieldValue(deltaMainDeltaSqlQueryRange);
            field6.setIndexType(IndexType.DELTA);
            sourceConfigurationFields.add(field6);
        }

        ConfigurationFields field7 = null;
        if(StringUtils.isNotEmpty(deltaMainDeltaSqlQueryPostIndex)){
            field7 = new ConfigurationFields();
            field7.setConfigurationType(ConfigurationType.SOURCE);
            field7.setFieldKey("sql_query_post_index");
            field7.setFieldValue(deltaMainDeltaSqlQueryPostIndex);
            field7.setIndexType(IndexType.DELTA);
            sourceConfigurationFields.add(field7);
        }

        ConfigurationFields field8 = null;
        if(StringUtils.isNotEmpty(deltaMainDeltaSqlQueryRangeStep)){
            field8 = new ConfigurationFields();
            field8.setConfigurationType(ConfigurationType.SOURCE);
            field8.setFieldKey("sql_range_step");
            field8.setFieldValue(deltaMainDeltaSqlQueryRangeStep);
            field8.setIndexType(IndexType.DELTA);
            sourceConfigurationFields.add(field8);
        }

        configuration.setSourceConfigurationFields(sourceConfigurationFields);
    }
    
    private void createDistributedConfiguration(Configuration configuration) {
    	//Configuration
        ConfigurationTemplate[] searchConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_DISTRIBUTED_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(searchConfigurationTemplates.length, 1);
        
        configuration.setSearchConfigurationTemplate(searchConfigurationTemplates[0]);
        
        ConfigurationTemplate[] configurationConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.DISTRIBUTED_CONFIGURATION_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(configurationConfigurationTemplates.length, 1);
        
        configuration.setConfigurationTemplate(configurationConfigurationTemplates[0]);
	
    }

    private void createConfiguration(Configuration configuration, DataSource dataSource, String testDataSourceTableColumnId, String testDataSourceTableColumnField,
                                     String sqlQuery, String testDataSourceTableColumnSqlField, String tableName) {
        //Configuration
        ConfigurationTemplate[] searchConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(searchConfigurationTemplates.length, 1);

        configuration.setSearchConfigurationTemplate(searchConfigurationTemplates[0]);

        ConfigurationTemplate[] indexConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.INDEXER_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(indexConfigurationTemplates.length, 1);

        configuration.setIndexerConfigurationTemplate(indexConfigurationTemplates[0]);

        ConfigurationTemplate[] configurationConfigurationTemplates = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.CONFIGURATION_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertTrue(configurationConfigurationTemplates.length > 0);

        configuration.setConfigurationTemplate(configurationConfigurationTemplates[0]);


        // ConfigurationFields
        /*
        Set<ConfigurationFields> searchConfigurationFields = new HashSet<ConfigurationFields>();

        ConfigurationFields configurationField1 = new ConfigurationFields();
        configurationField1.setConfigurationType(ConfigurationType.SEARCH);
        configurationField1.setFieldKey("listen");
        configurationField1.setFieldValue(listenPort);
        configurationField1.setConfiguration(configuration);

        searchConfigurationFields.add(configurationField1);

        configuration.setSearchConfigurationFields(searchConfigurationFields);



        Set<ConfigurationFields> sourceConfigurationFields = new HashSet<ConfigurationFields>();

        ConfigurationFields configurationField2 = new ConfigurationFields();
        configurationField2.setConfigurationType(ConfigurationType.SOURCE);
        configurationField2.setFieldKey("sql_query");
        configurationField2.setFieldValue(sqlQuery);
        configurationField2.setIndexType(IndexType.MAIN);
        configurationField2.setConfiguration(configuration);

        sourceConfigurationFields.add(configurationField2);

        ConfigurationFields configurationField3 = new ConfigurationFields();
        configurationField3.setConfigurationType(ConfigurationType.SOURCE);
        configurationField3.setFieldKey("sql_query");
        configurationField3.setFieldValue(sqlQuery);
        configurationField3.setIndexType(IndexType.MAIN);
        configurationField3.setConfiguration(configuration);

        sourceConfigurationFields.add(configurationField3);

        configuration.setSourceConfigurationFields(sourceConfigurationFields);*/


        ///DataSource and FieldMapping
        configuration.setDatasource(dataSource);

        FieldMapping fieldMapping1 = new FieldMapping();
        fieldMapping1.setIndexField(testDataSourceTableColumnId);
        fieldMapping1.setSourceField(testDataSourceTableColumnId);
        fieldMapping1.setIndexFieldCommentary(testDataSourceTableColumnId);
        fieldMapping1.setIndexFieldType(IndexFieldType.SQL_ATTR_UINT);
        fieldMapping1.setIsId(true);

        FieldMapping fieldMapping2 = new FieldMapping();
        fieldMapping2.setIndexField(testDataSourceTableColumnField);
        fieldMapping2.setSourceField(testDataSourceTableColumnField);
        fieldMapping2.setIndexFieldCommentary(testDataSourceTableColumnField);
        fieldMapping2.setIndexFieldType(IndexFieldType.SQL_FIELD_STRING);
        fieldMapping2.setIsId(false);

        FieldMapping fieldMapping3 = new FieldMapping();
        fieldMapping3.setIndexField(testDataSourceTableColumnSqlField);
        fieldMapping3.setSourceField(testDataSourceTableColumnSqlField);
        fieldMapping3.setIndexFieldCommentary(testDataSourceTableColumnSqlField);
        fieldMapping3.setIndexFieldType(IndexFieldType.SQL_FIELD);
        fieldMapping3.setIsId(false);

        LinkedHashSet<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();
        fieldMappings.add(fieldMapping1);
        fieldMappings.add(fieldMapping2);
        fieldMappings.add(fieldMapping3);

        SourceWrapper sourceWrapper = new SourceWrapper();
        sourceWrapper.setDatasource(dataSource);
        sourceWrapper.setTableName(tableName);

        ResponseEntity<List> result = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.QUERY_META_DATA, sourceWrapper, List.class);
        ObjectMapper objectMapper = new ObjectMapper();
        DBTableColumn column = null;
        for(Object map : result.getBody()){
            column = objectMapper.convertValue(map, DBTableColumn.class);
            for(FieldMapping fieldMapping : fieldMappings){
                if(fieldMapping.getSourceField().equals(column.getName())){
                    fieldMapping.setSourceFieldType(column.getType());
                }
            }
        }

        configuration.setFieldMappings(fieldMappings);
    }

    public void checkIfServerProcessAvailable(String serverIp, Integer sshPort, String username, String password, int serverPort) {
        String message = runSshCommand(serverIp, sshPort, username, password, "netstat -vatn | grep " + serverPort);
        logger.info("MESSAGE: " + message);
        Assert.assertNotSame(-1, message.indexOf("LISTEN"));
    }
    
    public void checkIfServerProcessNotAvailable(String serverIp, Integer sshPort, String username, String password, int serverPort) {
      String message = runSshCommand(serverIp, sshPort, username, password, "netstat -vatn | grep " + serverPort);
        logger.info("MESSAGE: " + message);
        Assert.assertSame(-1, message.indexOf("LISTEN"));
    }


    public void checkIfSphinxDataFolderAvailable(String serverIp, Integer sshPort, String username, String password, int serverPort, String processName) {
        String message = runSshCommand(serverIp, sshPort, username, password, "find /opt/sphinx.console/sphinx/data/searching/" + processName);
        logger.info("MESSAGE: " + message);
        Assert.assertEquals("", message);
    }

    public static JdbcTemplate createJdbcTemplate(String driverClassName, String jdbcUrl, String jdbcUsername, String jdbcPassword){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);

        return new JdbcTemplate(dataSource);
    }

    public void clearConfigurationDataBase(String jdbcUrl, String jdbcUsername, String jdbcPassword, String clearDataSqlFilePath, String startDataSqlFilePath) throws ClassNotFoundException, SQLException {
        runScriptFile(DataSourceType.MYSQL, jdbcUrl, jdbcUsername, jdbcPassword, clearDataSqlFilePath);
        runScriptFile(DataSourceType.MYSQL, jdbcUrl, jdbcUsername, jdbcPassword, startDataSqlFilePath);
    }

    public void runScriptFile(DataSourceType dataSourceType, String jdbcUrl, String jdbcUsername, String jdbcPassword, String filePath) throws ClassNotFoundException, SQLException {

        boolean hasException = false;

        Class.forName(dataSourceType.getDriverClass());
        Connection con = DriverManager.getConnection(
                jdbcUrl, jdbcUsername, jdbcPassword);
        Statement stmt = null;

        try {
            // Initialize object for ScripRunner
            ScriptRunner sr = new ScriptRunner(con, false, true);

            // Give the input file to Reader
            Reader reader1 = new BufferedReader(
                    new FileReader(filePath));

            // Exctute script
            sr.runScript(reader1);

        } catch (Exception e) {
            System.err.println("Failed to Execute " + filePath
                    + " The error is " + e.getMessage());
            hasException = true;
        } finally {
            Assert.assertEquals(false, hasException);
        }

    }


    private void checkStopAllProcesses(String collectionName, List<TaskDataViewWrapper> startTasks) throws InterruptedException {
        List<TaskDataViewWrapper> endTasks = taskService.getTasks(collectionName);

        List<TaskDataViewWrapper> tasks = (List<TaskDataViewWrapper>) CollectionUtils.disjunction(startTasks, endTasks);

        logger.info("TASKS: " + tasks);
        logger.info("TASKS, SIZE: " + tasks.size());

        for (Object taskDataViewWrapper : tasks) {
            logger.info("TASK NAME: " + ((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName"));
            logger.info("TASK STATUS: " + ((java.util.LinkedHashMap)taskDataViewWrapper).get("status"));
            if (((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName") != null && !((String)((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName")).equals("stopAllProcesses")) {
                continue;
            }
            Assert.assertEquals("stopAllProcesses", ((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName"));

            TaskStatus taskStatusInitial = TaskStatus.valueOf((String)((java.util.LinkedHashMap)taskDataViewWrapper).get("status"));

            if (taskStatusInitial != TaskStatus.SUCCESS && taskStatusInitial != TaskStatus.FAILURE) {
                //Отслеживание процесса создания по логам
                inner: while (true) {

                    TaskStatus taskStatus = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASK_STATUS, TaskStatus.class, (String)((java.util.LinkedHashMap)taskDataViewWrapper).get("taskUid"));

                    logger.info("TASK STATUS AFTER NEW RETRIEVE: " + taskStatus);
                    if (taskStatus == TaskStatus.SUCCESS || taskStatus == TaskStatus.FAILURE) {
                        break inner;
                    }

                    Thread.currentThread().sleep(3000);
                }

            }
        }
    }

    public void stopAllProcesses(String collectionName) throws InterruptedException {
    	logger.info("SERVER URI: " + serviceUtils.serverURI);

    	List<TaskDataViewWrapper> startTasks = taskService.getTasks(collectionName);
    	
    	Map<String, String> vars = new HashMap<String, String>();
        vars.put("collectionName", collectionName);

    	ResponseEntity<Status> responseEntity =
                 serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.STOP_ALL_PROCESSES, null, Status.class, vars);

        logger.info("RESPONSE: " + responseEntity);
        logger.info("RESPONSE CODE: " + responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());

        checkStopAllProcesses(collectionName, startTasks);
    }

    public void stopAllProcesses(String changesetPath, String collectionName) throws InterruptedException {
        logger.info("SERVER URI: " + serviceUtils.serverURI);

        List<TaskDataViewWrapper> startTasks = taskService.getTasks(collectionName);

        changesetRepository.execute(changesetPath, false);

        checkStopAllProcesses(collectionName, startTasks);
    }

    private void checkStartAllProcesses(String collectionName, List<TaskDataViewWrapper> startTasks) throws InterruptedException {
        List<TaskDataViewWrapper> endTasks = taskService.getTasks(collectionName);

        List<TaskDataViewWrapper> tasks = (List<TaskDataViewWrapper>) CollectionUtils.disjunction(startTasks, endTasks);

        logger.info("TASKS: " + tasks);
        logger.info("TASKS, SIZE: " + tasks.size());

        for (Object taskDataViewWrapper : tasks) {
            logger.info("TASK NAME: " + ((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName"));
            logger.info("TASK STATUS: " + ((java.util.LinkedHashMap)taskDataViewWrapper).get("status"));
            Assert.assertEquals("startAllProcesses", ((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName"));

            TaskStatus taskStatusInitial = TaskStatus.valueOf((String)((java.util.LinkedHashMap)taskDataViewWrapper).get("status"));

            if (taskStatusInitial != TaskStatus.SUCCESS && taskStatusInitial != TaskStatus.FAILURE) {
                //Отслеживание процесса создания по логам
                inner: while (true) {

                    TaskStatus taskStatus = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASK_STATUS, TaskStatus.class, (String)((java.util.LinkedHashMap)taskDataViewWrapper).get("taskUid"));

                    logger.info("TASK STATUS AFTER NEW RETRIEVE: " + taskStatus);
                    if (taskStatus == TaskStatus.SUCCESS || taskStatus == TaskStatus.FAILURE) {
                        break inner;
                    }

                    Thread.currentThread().sleep(3000);
                }
            }
        }
    }

    public void startAllProcesses(String collectionName) throws InterruptedException  {
        logger.info("SERVER URI: " + serviceUtils.serverURI);

    	List<TaskDataViewWrapper> startTasks = taskService.getTasks(collectionName);
    	    	
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("collectionName", collectionName);

    	ResponseEntity<Status> responseEntity = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.START_ALL_PROCESSES, null, Status.class, vars);

        logger.info("RESPONSE: " + responseEntity);
    	logger.info("RESPONSE CODE: " + responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());

        checkStartAllProcesses(collectionName, startTasks);
    }

    public void startAllProcesses(String changesetPath, String collectionName) throws InterruptedException  {
        logger.info("SERVER URI: " + serviceUtils.serverURI);

        List<TaskDataViewWrapper> startTasks = taskService.getTasks(collectionName);

        changesetRepository.execute(changesetPath, false);

        checkStartAllProcesses(collectionName, startTasks);
    }

    public void getReplicasDataForStopProcesses(String collectionName, String serverIp, Integer sshPort, String username, String password) {
        logger.info("SERVER URI: " + serviceUtils.serverURI);
    	
    	Map<String, String> vars = new HashMap<String, String>();
        vars.put("collectionName", collectionName);

        java.util.LinkedHashMap replicasData = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.REPLICAS_DATA, java.util.LinkedHashMap.class, vars);
         
        logger.info("REPLICAS DATA: " + replicasData.get("list"));

        java.util.ArrayList<java.util.LinkedHashMap> replicas =  (java.util.ArrayList<java.util.LinkedHashMap>) replicasData.get("list");
         
        for (java.util.LinkedHashMap object : replicas) {
            logger.info("REPLICA STATUS: " + object.get("searchServerStatus") + ", SEARCH PORT: " + object.get("searchPort") + ", SERVER HOST: " + (String)((Map)object.get("server")).get("ip"));
        	Assert.assertEquals(Boolean.FALSE, object.get("searchServerStatus"));
        	 
        	String serverHost = (String)((Map)object.get("server")).get("ip");
        	 
        	checkIfServerProcessNotAvailable(serverHost, 22, username, password, (Integer)object.get("searchPort"));
        }
    }
    
    public void mergeCollection (String collectionName){
        logger.info("Merge collection: " + collectionName);
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, null, Status.class, collectionName);
        checkStatus(status.getTaskUID());
    }

    public void repeatMergeFails(String collectionName){
        logger.info("Merge collection: " + collectionName);
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, null, Status.class, collectionName);
        Assert.assertTrue(status.getCode() == 0);
        status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, null, Status.class, collectionName);
        Assert.assertTrue(status.getCode() == 16);
    }

    public void getReplicasDataForStartProcesses(String collectionName, String serverIp, Integer sshPort, String username, String password) {
    	logger.info("SERVER URI: " + serviceUtils.serverURI);
    	
    	Map<String, String> vars = new HashMap<String, String>();
        vars.put("collectionName", collectionName);

        java.util.LinkedHashMap replicasData = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.REPLICAS_DATA, java.util.LinkedHashMap.class, vars);

        logger.info("REPLICAS DATA: " + replicasData.get("list"));

        java.util.ArrayList<java.util.LinkedHashMap> replicas =  (java.util.ArrayList<java.util.LinkedHashMap>) replicasData.get("list");
         
        for (java.util.LinkedHashMap object : replicas) {
            logger.info("REPLICA STATUS: " + object.get("searchServerStatus") + ", SEARCH PORT: " + object.get("searchPort"));
            Assert.assertEquals(Boolean.TRUE, object.get("searchServerStatus"));

            String serverHost = (String)((Map)object.get("server")).get("ip");

            checkIfServerProcessAvailable(serverHost, 22, username, password, (Integer)object.get("searchPort"));
        }
    }

    private void checkCreateReplica(String taskUID, String collectionName, String serverIp, Integer port) throws Throwable {
        logger.info("UID: " + taskUID);
        checkStatus(taskUID);
        logger.info("CHECK STATUS DONE");
        Long collectionSize = getCollectionSize(collectionName, serverIp, port);
        Assert.assertTrue(collectionSize > 0);
    }

    public ReplicaWrapper buildReplicaWrapper(String collectionName, Server server, Integer port, Integer distributedPort) {
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        replicaWrapper.setCollectionName(collectionName);
        replicaWrapper.setServer(server);
        replicaWrapper.setDistributedPort(distributedPort);
        replicaWrapper.setSearchPort(port);
        return replicaWrapper;
    }

    public void createReplica(String collectionName, Server server, Integer port, Integer distributedPort) throws Throwable {
        logger.info("SERVER NAME: " + server.getName());
        ReplicaWrapper replicaWrapper = buildReplicaWrapper(collectionName, server, port, distributedPort);

        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.CREATE_REPLICA, replicaWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(replicaWrapper);
        Assert.assertEquals(status.getCode(), 0);

        checkCreateReplica(status.getTaskUID(), collectionName, server.getIp(), port);
    }

    public void createReplica(String changesetPath, String collectionName, Server server, Integer port) throws Throwable {
        logger.info("SERVER NAME: " + server.getName());

        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkCreateReplica(lastCommandResult.getTaskUid(), collectionName, server.getIp(), port);
    }

    private void checkRemoveReplica(String taskUID, String collectionName, Long replicaNumber,
                                    String searchingAgentServerIp, String searchingAgentServerRootPassword) {
        logger.info("UID: " + taskUID);
        checkStatus(taskUID);

        Assert.assertTrue(findReplicaWrapper(collectionName, replicaNumber) == null);

        String processName = collectionName + "_" + replicaNumber;
        String collectionRootDirectory = "/opt/sphinx.console/sphinx/";
        Assert.assertFalse(isPathExists(searchingAgentServerIp, 22, "root", searchingAgentServerRootPassword, collectionRootDirectory + "data/indexing/" + processName));
        Assert.assertFalse(isPathExists(searchingAgentServerIp, 22, "root", searchingAgentServerRootPassword, collectionRootDirectory + "log/indexing/" + processName));
        Assert.assertFalse(isPathExists(searchingAgentServerIp, 22, "root", searchingAgentServerRootPassword, collectionRootDirectory + "pid/indexing/" + processName));
        Assert.assertFalse(isPathExists(searchingAgentServerIp, 22, "root", searchingAgentServerRootPassword, collectionRootDirectory + "cfg/indexing/" + processName));
        Assert.assertFalse(isPathExists(searchingAgentServerIp, 22, "root", searchingAgentServerRootPassword, collectionRootDirectory + "binlog/indexing/" + processName));

        Assert.assertFalse(isPathExists(searchingAgentServerIp, 22, "root", searchingAgentServerRootPassword, "/etc/init.d/sphinx.console-service_" + processName));
    }

    public void removeReplica(String changesetPath, String collectionName, Long replicaNumber, String searchingAgentServerIp, String searchingAgentServerRootPassword) {
        logger.info("COLLECTION NAME : " + collectionName + " REPLICA NUMBER: " + replicaNumber);

        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkRemoveReplica(lastCommandResult.getTaskUid(), collectionName, replicaNumber, searchingAgentServerIp, searchingAgentServerRootPassword);
    }

    public void removeReplica(String collectionName, Long replicaNumber, String searchingAgentServerIp, String searchingAgentServerRootPassword) {
        logger.info("COLLECTION NAME : " + collectionName + " REPLICA NUMBER: " + replicaNumber);
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        replicaWrapper.setCollectionName(collectionName);
        replicaWrapper.setReplicaNumber(replicaNumber);
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.REMOVE_REPLICA, replicaWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.writeValueAsString(replicaWrapper);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        Assert.assertEquals(status.getCode(), 0);

        checkRemoveReplica(status.getTaskUID(), collectionName, replicaNumber, searchingAgentServerIp, searchingAgentServerRootPassword);
    }

    public ReplicaWrapper findReplicaWrapper(String collectionName, Long replicaNumber) {
        ListDataViewWrapper<List> replicasData = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.REPLICAS_DATA, ListDataViewWrapper.class, collectionName);
        ReplicaWrapper result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object map : replicasData.getList()) {
            ReplicaWrapper replicaWrapper = objectMapper.convertValue(map, ReplicaWrapper.class);
            if(replicaWrapper.getReplicaNumber().equals(replicaNumber)) {
                return replicaWrapper;
            }
        }

        return result;
    }

    public List<ReplicaWrapper> getReplicaWrappers(String collectionName) {
        ListDataViewWrapper<List> replicasData = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.REPLICAS_DATA, ListDataViewWrapper.class, collectionName);
        List<ReplicaWrapper> list = new ArrayList<ReplicaWrapper>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object map : replicasData.getList()) {
            ReplicaWrapper replicaWrapper = objectMapper.convertValue(map, ReplicaWrapper.class);
            list.add(replicaWrapper);
        }

        return list;
    }

    public ReplicaWrapper findReplicaWrapperAnyBySearchPort(String collectionName, String ip, Integer searchPort) {
        ListDataViewWrapper<List> replicasData = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.REPLICAS_DATA, ListDataViewWrapper.class, collectionName);
        ReplicaWrapper result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object map : replicasData.getList()) {
            ReplicaWrapper replicaWrapper = objectMapper.convertValue(map, ReplicaWrapper.class);
            if(replicaWrapper.getServer().getIp().equals(ip) && replicaWrapper.getSearchPort().equals(searchPort)) {
                return replicaWrapper;
            }
        }

        return result;
    }

    public boolean isPathExists(String serverIp, int sshPort, String username, String password, String path) {
        String message = runSshCommand(serverIp, sshPort, username, password, "find " + path);
        logger.info("PATH: " + path);
        logger.info("MESSAGE: " + message);
        return !message.isEmpty();
    }

    private void checkStatus(int statusCode, String taskUID) {
        checkCode(statusCode);

        logger.info("UID: " + taskUID);
        checkStatus(taskUID);
    }

    private void checkCode(int statusCode) {
        logger.info("Status code: " + statusCode);
        Assert.assertEquals(statusCode, 0);
    }

    public void makeCollectionFullRebuildIndex(String collectionName, String serverName) {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_INDEX, null, Status.class, collectionName, serverName);

        checkStatus(status.getCode(), status.getTaskUID());
    }

    public void executeChangesetWithCheck(String changesetPath) {
        CommandResult lastCommandResult = changesetRepository.execute(changesetPath, false);

        checkStatus(lastCommandResult.getCode(), lastCommandResult.getTaskUid());
    }

    public CommandResult executeChangeset(String changesetPath) {
        return changesetRepository.execute(changesetPath, false);
    }

    public CommandResult executeChangeset(String changesetPath, String propsPath) {
        return changesetRepository.execute(changesetPath, propsPath, false);
    }

    public void deleteFullIndexData(String collectionName) {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.DELETE_FULL_INDEX_DATA, null, Status.class, collectionName);

        checkStatus(status.getCode(), status.getTaskUID());
    }

    public void makeCollectionFullRebuildApply(String collectionName, String serverName) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_APPLY, null, Status.class, collectionName, serverName);

        logger.info("MAKE_COLLECTION_FULL_REBUILD_APPLY STATUS: " + status);

        checkStatus(status.getCode(), status.getTaskUID());
    }

    public void makeCollectionFullRebuildApplyFailure(String collectionName, String serverName) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_APPLY, null, Status.class, collectionName, serverName);

        logger.info("MAKE_COLLECTION_FULL_REBUILD_APPLY STATUS: " + status.getCode());
        Assert.assertEquals(status.getCode(), 15);
        Assert.assertNull(status.getTaskUID());
    }

    private java.util.ArrayList<java.util.LinkedHashMap> getReplicas(String collectionName) {
        // Буферизуем реплики
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("collectionName", collectionName);

        java.util.LinkedHashMap replicasData = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.REPLICAS_DATA, java.util.LinkedHashMap.class, vars);

        logger.info("REPLICAS DATA: " + replicasData.get("list"));

        return (java.util.ArrayList<java.util.LinkedHashMap>) replicasData.get("list");
    }

    public void checkNeedReloadCollection(String collectionName, String jdbcUrl, String jdbcUsername, String jdbcPassword, Boolean expectedValue) {
        //Проверяыем что в БД координатора изменилось поле need_reload
        Integer result = createJdbcTemplate("com.mysql.jdbc.Driver", jdbcUrl, jdbcUsername, jdbcPassword)
                .queryForObject("select need_reload from sphinx.console.COLLECTION where collection_name = ?", Integer.class, collectionName);
        Boolean actualValue = result != null && result > 0;
        logger.info("COLLECTION: " + collectionName);
        Assert.assertEquals(expectedValue, actualValue);
    }

    private void checkCollection(String collectionName, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
        //Проверяыем что в БД координатора коллеции нет
        List<Collection> collections = createJdbcTemplate("com.mysql.jdbc.Driver", jdbcUrl, jdbcUsername, jdbcPassword)
                .query("select * from sphinx.console.COLLECTION where collection_name = ?", new RowMapper<Collection>() {
                    @Override
                    public Collection mapRow(ResultSet resultSet, int i) throws SQLException {
                        Collection collection = new Collection();
                        collection.setName(resultSet.getString("collection_name"));
                        return collection;
                    }
                }, collectionName);

        logger.info("COLLECTION: " + collections);

        Assert.assertTrue(collections.isEmpty());
    }

    private void checkReplicas(String collectionName, java.util.ArrayList<java.util.LinkedHashMap> replicas, String username, String password, boolean isNeedReplicaNumber) {
        for (java.util.LinkedHashMap object : replicas) {
            logger.info("BUFFERED REPLICA STATUS: " + object.get("searchServerStatus") + ", SEARCH PORT: " + object.get("searchPort") +
                    (isNeedReplicaNumber ? ", REPLICA NUMBER: " + object.get("replicaNumber") : ""));
            logger.info("BUFFERED REPLICA SERVER IP: " + ((Map)object.get("server")).get("ip"));

            String serverHost = (String)((Map)object.get("server")).get("ip");

            if(isNeedReplicaNumber) {
                checkIfSphinxDataFolderAvailable(serverHost, 22, username, password, (Integer) object.get("searchPort"), collectionName + "_" + object.get("replicaNumber"));
            } else {
                checkIfServerProcessNotAvailable(serverHost, 22, username, password, (Integer) object.get("searchPort"));
            }
        }
    }

    private void checkTasks(String collectionName, List<TaskDataViewWrapper> startTasks) throws InterruptedException {
        List<TaskDataViewWrapper> endTasks = taskService.getTasks(collectionName);
        List<TaskDataViewWrapper> tasks = (List<TaskDataViewWrapper>) CollectionUtils.disjunction(startTasks, endTasks);

        logger.info("TASKS: " + tasks);
        logger.info("TASKS, SIZE: " + tasks.size());

        for (Object taskDataViewWrapper : tasks) {
            logger.info("TASK NAME: " + ((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName"));
            logger.info("TASK STATUS: " + ((java.util.LinkedHashMap)taskDataViewWrapper).get("status"));
            if (((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName") != null && !((String)((java.util.LinkedHashMap)taskDataViewWrapper).get("taskName")).equals("deleteCollection")) {
                continue;
            }
            Assert.assertEquals("deleteCollection", ((java.util.LinkedHashMap) taskDataViewWrapper).get("taskName"));

            TaskStatus taskStatusInitial = TaskStatus.valueOf((String)((java.util.LinkedHashMap)taskDataViewWrapper).get("status"));

            if (taskStatusInitial != TaskStatus.SUCCESS && taskStatusInitial != TaskStatus.FAILURE) {
                //Отслеживание процесса удаления по логам
                inner: while (true) {

                    TaskStatus taskStatus = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASK_STATUS, TaskStatus.class, (String)((java.util.LinkedHashMap)taskDataViewWrapper).get("taskUid"));

                    logger.info("TASK STATUS AFTER NEW RETRIEVE: " + taskStatus);
                    if (taskStatus == TaskStatus.SUCCESS || taskStatus == TaskStatus.FAILURE) {
                        break inner;
                    }

                    Thread.currentThread().sleep(3000);
                }
            }
        }
    }

    private void checkDeleteCollection(String collectionName, List<TaskDataViewWrapper> startTasks, java.util.ArrayList<java.util.LinkedHashMap> replicas, String jdbcUrl, String jdbcUsername, String jdbcPassword, String username, String password) throws InterruptedException {
        checkTasks(collectionName, startTasks);

        checkCollection(collectionName, jdbcUrl, jdbcUsername, jdbcPassword);

        //Проверяем что по буферизованным репликам процессы не прослушиваются
        checkReplicas(collectionName, replicas, username, password, false);

        //Проверяем наличие папок с данными
        checkReplicas(collectionName, replicas, username, password, true);
    }

    public void deleteCollection(String chagesetPath, String collectionName, String jdbcUrl, String jdbcUsername, String jdbcPassword, String username, String password) throws InterruptedException {
        logger.info("SERVER URI: " + serviceUtils.serverURI);

        java.util.ArrayList<java.util.LinkedHashMap> replicas = getReplicas(collectionName);

        List<TaskDataViewWrapper> startTasks = taskService.getTasks(collectionName);

        // call coordinator
        executeChangeset(chagesetPath);

        checkDeleteCollection(collectionName, startTasks, replicas, jdbcUrl, jdbcUsername, jdbcPassword, username, password);
    }

    public void deleteCollection(String collectionName, String jdbcUrl, String jdbcUsername, String jdbcPassword, String username, String password) throws InterruptedException {
        logger.info("SERVER URI: " + serviceUtils.serverURI);
        
        java.util.ArrayList<java.util.LinkedHashMap> replicas = getReplicas(collectionName);

        List<TaskDataViewWrapper> startTasks = taskService.getTasks(collectionName);
   	    	
        // call coordinator
        ResponseEntity<Status> responseEntity = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.DELETE_COLLECTION, null, Status.class, collectionName);

        logger.info("RESPONSE: " + responseEntity);
        logger.info("RESPONSE CODE: " + responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
        //

        checkDeleteCollection(collectionName, startTasks, replicas, jdbcUrl, jdbcUsername, jdbcPassword, username, password);
    }

    public void modifyPortReplicaDistributedCollection(ReplicaWrapper replicaWrapper) {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_DISTRIBUTED_REPLICA_PORT, replicaWrapper, Status.class);
        checkStatus(status.getTaskUID());
    }

    public void modifyPortReplicaFromSimpleCollection(String distributedCollectionName, ReplicaWrapper replicaWrapper, String jdbcUrl, String jdbcUsername, String jdbcPassword, Boolean expectedValue) {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_REPLICA_PORT, replicaWrapper, Status.class);
        checkStatus(status.getTaskUID());
        checkNeedReloadCollection(distributedCollectionName, jdbcUrl, jdbcUsername, jdbcPassword, expectedValue);
    }

    public void deleteReplicaFromSimpleCollection(String distributedCollectionName, String collectionName, Long replicaNumber, String searchingAgentServerIp, String searchingAgentServerRootPassword,
                                                  String jdbcUrl, String jdbcUsername, String jdbcPassword, Boolean expectedValue) {
        removeReplica(collectionName, replicaNumber, searchingAgentServerIp, searchingAgentServerRootPassword);
        checkNeedReloadCollection(distributedCollectionName, jdbcUrl, jdbcUsername, jdbcPassword, expectedValue);
    }

    public void reloadDistributedCollection(String collectionName, Boolean expectedValue, String jdbcUrl, String jdbcUsername, String jdbcPassword) throws InterruptedException {
        ResponseEntity<Status> responseEntity =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.RELOAD_DISTRIBUTED_COLLECTION, null, Status.class, collectionName);
        checkStatus(responseEntity.getBody().getTaskUID());
        checkNeedReloadCollection(collectionName, jdbcUrl, jdbcUsername, jdbcPassword, expectedValue);
    }

    public void reloadDistributedCollection(String changesetPath, String propsPath, String collectionName, Boolean expectedValue, String jdbcUrl, String jdbcUsername, String jdbcPassword) throws InterruptedException {
        executeChangeset(changesetPath, propsPath);
        checkNeedReloadCollection(collectionName, jdbcUrl, jdbcUsername, jdbcPassword, expectedValue);
    }

    public Server setUpLocalServer(String jdbcUrl, String jdbcUsername, String jdbcPassword, String serverIp) {
        Server server = null;
        try {
            server = createJdbcTemplate("com.mysql.jdbc.Driver", jdbcUrl, jdbcUsername, jdbcPassword)

                    .queryForObject("select * from sphinx.console.SERVER where ip = ?", new RowMapper<Server>() {
                        @Override
                        public Server mapRow(ResultSet resultSet, int i) throws SQLException {
                            Server server = new Server();
                            server.setName(resultSet.getString("name"));
                            server.setDomain(resultSet.getString("domain_name"));
                            server.setId(resultSet.getLong("server_id"));
                            server.setIp(resultSet.getString("ip"));
                            return server;
                        }
                    }, serverIp);
            logger.info("SERVER: " + server);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            logger.info("Can't find server with ip: " + serverIp);
        }

        return server;
    }

    public void fullIndexingStateReadyForApply(String collectionName) {
        Assert.assertTrue(getCollectionInfo(collectionName).getFullIndexingResult().getFullIndexingState() == FullIndexingState.READY_FOR_APPLY);
    }

    public void checkQuery(List<String> validQueries, List<String> inValidQueries, String testDataSourceHost,
                           String testDataSourceType, Integer testDataSourcePort, String testDataSourceUsername, String testDataSourcePassword, String testDatasourceDB) {

        SourceWrapper sourceWrapper = new SourceWrapper();
        DataSource dataSource = new DataSource();
        dataSource.setHost(testDataSourceHost);
        dataSource.setType(DataSourceType.getByTitle(testDataSourceType));
        dataSource.setPort(testDataSourcePort);
        dataSource.setUser(testDataSourceUsername);
        dataSource.setPassword(testDataSourcePassword);
        dataSource.setSqlDb(testDatasourceDB);
        sourceWrapper.setDatasource(dataSource);

        for(String validQuery : validQueries) {
            sourceWrapper.setMainSqlQuery(validQuery);

            ResponseEntity<Object> result = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.QUERY_META_DATA, sourceWrapper, Object.class);

            logger.info(result.getBody().toString());

            if(result.getBody() instanceof Map && ((Map)result.getBody()).get("code") != null)
            {
                Assert.assertTrue("Problem with query : " + validQuery, false);
            }
        }
    }

    public void getSphinxQLConsoleResult(String serverName, Integer searchdPort, String collectionName) {
        logger.info(MessageFormat.format("--- START sphinxQLQueryTest serverName = {0}, searchdPort = {1}, collectionName = {2}---",serverName, searchdPort, collectionName));

        String query = MessageFormat.format(SPHINX_QL_QUERY_TEMPLATE, collectionName);
        RequestWrapper<String> queryWrapper = new RequestWrapper<String>();
        queryWrapper.setParameter(query);

        String url = serviceUtils.serverURI + CoordinatorViewRestURIConstants.SPHINXQL_QUERY_RESULT;
        ResponseEntity<SphinxQLMultiResult> result = serviceUtils.REST_TEMPLATE.postForEntity(url, queryWrapper, SphinxQLMultiResult.class, serverName, searchdPort);
        Status status = result.getBody().getStatus();
        if(status.getCode() != 0){
            logger.error("Error during sphinxql request. Status: " + status.toString() + status.getStackTrace());
        }

        Assert.assertEquals(result.getBody().getStatus().getCode(), 0);
    }

    public List<SearchQueryGrouped> getSearchQueriesResultsGrouped(SearchQuerySearchParameters searchParameters) {

        String url = serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_QUERIES_RESULTS_GROUPED;
        ResponseEntity<ListDataViewWrapper> response = serviceUtils.REST_TEMPLATE.postForEntity(url, searchParameters, ListDataViewWrapper.class);

        List<SearchQueryGrouped> list = convertResponseList(response.getBody(), SearchQueryGrouped.class);
        return list;
    }

    public <T> List<T> convertResponseList(ListDataViewWrapper<List<T>> source, Class targetClass) {
        List<T> list = new ArrayList<T>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object map : source.getList()) {
            list.add((T)objectMapper.convertValue(map, targetClass));
        }

        return list;
    }

    public List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(SearchQueryHistorySearchParameters searchParameters) {

        String url = serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_TOTAL_TIME;
        ResponseEntity<List> response = serviceUtils.REST_TEMPLATE.postForEntity(url, searchParameters, List.class);

        return converterService.convertResponseList(response.getBody(), SearchQueryHistoryPoint.class);
    }

    public List<SearchQueryHistoryPoint> getQueryHistoryResultCount(SearchQueryHistorySearchParameters searchParameters) {

        String url = serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_RESULT_COUNT;
        ResponseEntity<List> response = serviceUtils.REST_TEMPLATE.postForEntity(url, searchParameters, List.class);

        return converterService.convertResponseList(response.getBody(), SearchQueryHistoryPoint.class);
    }

    public List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(SearchQueryHistorySearchParameters searchParameters) {

        String url = serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_QUERY_COUNT;
        ResponseEntity<List> response = serviceUtils.REST_TEMPLATE.postForEntity(url, searchParameters, List.class);

        return converterService.convertResponseList(response.getBody(), SearchQueryHistoryPoint.class);
    }

    public List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(SearchQueryHistorySearchParameters searchParameters) {

        String url = serviceUtils.serverURI + CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_OFFSET_NOT_ZERO_COUNT;
        ResponseEntity<List> response = serviceUtils.REST_TEMPLATE.postForEntity(url, searchParameters, List.class);

        return converterService.convertResponseList(response.getBody(), SearchQueryHistoryPoint.class);
    }

    public void runAdminJar(String sshClient, String ip, String user, String configLocation, String jarFilePath, String changesetFilePath) {
        ProcessStatus processStatus = ProcessStatus.FAILURE;
        try {
            String command = MessageFormat.format("{0} {1}@{2} \"java -DconfigLocation={3} -jar {4} {5}\"", sshClient, user, ip, configLocation, jarFilePath, changesetFilePath);
            logger.info("ABOUT TO RUN ADMIN COMMAND: " + command);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            String line = null;
            String text = "";
            while ((line = input.readLine()) != null) {
                text += line;
            }
            input.close();

            BufferedReader error =
                    new BufferedReader
                            (new InputStreamReader(process.getErrorStream()));
            line = null;
            String errorText = "";
            while ((line = error.readLine()) != null) {
                errorText += line;
            }
            error.close();

            process.waitFor();
            logger.info("COMMAND RESULT: " + process.exitValue());
            logger.info("COMMAND CONSOLE LOG: " + text);
            logger.info("COMMAND CONSOLE ERROR LOG: " + errorText);
            Assert.assertEquals(0, process.exitValue());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("PROCESS STATUS: " + processStatus.toString());
    }

    public void stopProcess(String collectionName, Long replicaNumber, String ip, Integer port) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.STOP_PROCESS, null,
                Status.class, collectionName, replicaNumber);
        checkStatus(status.getTaskUID());
        checkSearchProcessUnavailable(ip, port);
    }

    public void createSnippet(SnippetConfigurationWrapper snippetConfigurationWrapper) throws Throwable {
        ResponseEntity<Status> responseEntity =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.CREATE_SNIPPET_CONFIGURATION, snippetConfigurationWrapper, Status.class);

        // Старт проверок на корректность создания
        logger.info("" + responseEntity.getBody());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
        checkStatus(responseEntity.getBody().getTaskUID());
    }

    public void editSnippet(SnippetConfigurationWrapper snippetConfigurationWrapper) throws Throwable {
        ResponseEntity<Status> responseEntity =
                serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.EDIT_SNIPPET_CONFIGURATION, snippetConfigurationWrapper, Status.class);

        // Старт проверок на корректность создания
        logger.info("" + responseEntity.getBody());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
        checkStatus(responseEntity.getBody().getTaskUID());
    }

    public void deleteSnippet(String collectionName) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.DELETE_SNIPPET_CONFIGURATION, Status.class, collectionName);

        logger.info("" + status.getCode());
        Assert.assertEquals(0, status.getCode());
    }

    public void rebuildSnippets(String collectionName) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.REBUILD_SNIPPETS, Status.class, collectionName);

        logger.info("" + status.getCode());
        Assert.assertEquals(0, status.getCode());
        checkStatus(status.getTaskUID());
    }

    public void fullRebuildSnippets(String collectionName) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.MAKE_SNIPPETS_FULL_REBUILD, Status.class, collectionName);

        logger.info("" + status.getCode());
        Assert.assertEquals(0, status.getCode());
        checkStatus(status.getTaskUID());
    }

    public void stopSnippetFullRebuildExecution(String collectionName) throws Throwable {
        Status status = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.STOP_FULL_REBUILD_SNIPPETS, Status.class, collectionName);

        logger.info("" + status.getCode());
        Assert.assertEquals(0, status.getCode());
    }

    public SnippetConfigurationWrapper buildSnippetConfigurationWrapper(String collectionName, String cron)
            throws InterruptedException, SQLException, ClassNotFoundException {
        SnippetConfigurationWrapper snippetConfigurationWrapper = new SnippetConfigurationWrapper();

        snippetConfigurationWrapper.setCollectionName(collectionName);

        CronScheduleWrapper cronSchedule = new CronScheduleWrapper();
        cronSchedule.setCronSchedule(cron);
        snippetConfigurationWrapper.setCron(cronSchedule);

        SnippetConfiguration snippetConfiguration = new SnippetConfiguration();

        snippetConfiguration.setPreQuery("update test.files set snippet_pre_query=true where id=(select max(id) from test.files);");
        snippetConfiguration.setPostQuery("update test.files set snippet_post_query=true where id=(select max(id) from test.files);");
        snippetConfiguration.setMainQuery("select id, snippet_text from test.files");
        snippetConfiguration.setFullPreQuery("select id, snippet_text || \' " + MakeSnippetFullRebuildTest.SNIPPET_FULL_REBUILD_SUFFIX + "\' as snippet_text from test.files");
        snippetConfiguration.setFullPostQuery("select id, snippet_text || \' " + MakeSnippetFullRebuildTest.SNIPPET_FULL_REBUILD_SUFFIX + "\' as snippet_text from test.files");
        snippetConfiguration.setFullMainQuery("select id, snippet_text || \' " + MakeSnippetFullRebuildTest.SNIPPET_FULL_REBUILD_SUFFIX + "\' as snippet_text from test.files");

        Set<SnippetConfigurationField> fields = new HashSet<SnippetConfigurationField>();
        SnippetConfigurationField snippetConfigurationField = new SnippetConfigurationField();
        snippetConfigurationField.setSnippetConfiguration(snippetConfiguration);
        snippetConfigurationField.setFieldName("snippet_text");
        fields.add(snippetConfigurationField);

        snippetConfiguration.setFields(fields);

        snippetConfigurationWrapper.setSnippetConfiguration(snippetConfiguration);

        return snippetConfigurationWrapper;
    }

    public void checkSnippetFile(String collectionName, Integer id, String expectedText, String sshClient, String indexingServerIp, String searchingServerId) throws IOException, InterruptedException {
        String fileName = id + ".txt";
        String userName = "root";

        List<String> commands = new ArrayList<String>();
        String getSnippetTextInIndexingDirCommand = MessageFormat.format(FILE_TEXT_BY_FILE_NAME_TEMPLATE,sphinx.console_SPHINX_SNIPPET_DIR
                + sphinx.console_INDEXING_DIR_NAME + "/" + collectionName + "_1/*", fileName);
        commands.add(commandLineService.getSshCommandText(sshClient, indexingServerIp, userName, getSnippetTextInIndexingDirCommand));

        List<ReplicaWrapper> replicaWrappers = getReplicaWrappers(collectionName);
        String getSnippetTextInSearchingDirCommand;
        for(ReplicaWrapper replicaWrapper : replicaWrappers){
            getSnippetTextInSearchingDirCommand = MessageFormat.format(FILE_TEXT_BY_FILE_NAME_TEMPLATE,sphinx.console_SPHINX_SNIPPET_DIR
                    + sphinx.console_SEARCHING_DIR_NAME  + "/" + collectionName + "_" + replicaWrapper.getReplicaNumber() + "/*", fileName);
            commands.add(commandLineService.getSshCommandText(sshClient, indexingServerIp, userName, getSnippetTextInSearchingDirCommand));
        }

        String fileText;
        for(String command : commands){
            fileText = commandLineService.executeCommandWithResult(command);
            Assert.assertEquals(expectedText, fileText.trim());
        }
    }

    public List<SimpleCollectionWrapper> getAllSimpleCollections() {
        List<SimpleCollectionWrapper> result = new ArrayList<SimpleCollectionWrapper>();
        List<Map> simpleCollectionWrapperMaps = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SIMPLE_COLLECTIONS, List.class);

        ObjectMapper objectMapper = new ObjectMapper();
        for(Map map : simpleCollectionWrapperMaps){
            SimpleCollectionWrapper simpleCollectionWrapper = objectMapper.convertValue(map, SimpleCollectionWrapper.class);
            result.add(simpleCollectionWrapper);
        }

        return result;
    }

    public void deleteAllSimpleCollections(){
        String collectionName;
        for(SimpleCollectionWrapper collectionWrapper : getAllSimpleCollections()){
            collectionName = collectionWrapper.getCollectionName();
            logger.info("Delete collection: " + collectionName);
            Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.DELETE_COLLECTION, null, Status.class, collectionName);
            Assert.assertEquals(0, status.getCode());
        }
    }
}
