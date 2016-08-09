package ru.skuptsov.sphinx.console.test.integration.tests.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.test.integration.service.TestExecutor;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentHelper;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-context.xml"})
public class ValidationTest extends TestEnvironmentHelper {

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Autowired
    TestExecutor testExecutor;

    @Test
    public void addCollectionWithInvalidParamsFailureTest() throws Throwable {

        CollectionWrapper collectionWrapper = new CollectionWrapper();

        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.ADD_COLLECTION, collectionWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void modifyCollectionAttributesWithInvalidParamsFailureTest() throws Throwable {

        CollectionWrapper collectionWrapper = new CollectionWrapper();

        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_COLLECTION_ATTRIBUTES, collectionWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void addServerWithInvalidParamsFailureTest() {
        Server server = new Server();
        server.setName("server_name");
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.ADD_SERVER, server, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void addAdminProcessWithInvalidParamsFailureTest() {
        AdminProcess adminProcess = new AdminProcess();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.ADD_ADMIN_PROCESS, adminProcess, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void updateAdminProcessWithInvalidParamsFailureTest() {
        AdminProcess adminProcess = new AdminProcess();
        ResponseEntity<Status> response = REST_TEMPLATE.exchange(serverURI + CoordinatorConfigurationRestURIConstants.UPDATE_ADMIN_PROCESS, HttpMethod.PUT, new HttpEntity<AdminProcess>(adminProcess), Status.class);

        testExecutor.checkValidationFailure(response.getBody());
    }

    @Test
    public void changeCollectionUpdateScheduleWithInvalidParamsFailureTest() {
        UpdateScheduleWrapper updateScheduleWrapper = new UpdateScheduleWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.CHANGE_COLLECTION_UPDATE_SCHEDULE, updateScheduleWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void moveProcessToServerWithInvalidParamsFailureTest() {
        MoveProcessToServerWrapper moveProcessToServerWrapper = new MoveProcessToServerWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MOVE_PROCESS_TO_SERVER, moveProcessToServerWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void stopTaskWithInvalidParamsFailureTest() {
        TaskWrapper taskWrapper = new TaskWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.STOP_TASK, taskWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void pauseTaskWithInvalidParamsFailureTest() {
        TaskWrapper taskWrapper = new TaskWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.PAUSE_TASK, taskWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void resumeTaskWithInvalidParamsFailureTest() {
        TaskWrapper taskWrapper = new TaskWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.RESUME_TASK, taskWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void checkDBConnectionWithInvalidParamsFailureTest() {
        DataSource datasource = new DataSource();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.CHECK_DB_CONNECTION, datasource, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void addConfigurationTemplateWithInvalidParamsFailureTest() {
        ConfigurationTemplate configurationTemplate = new ConfigurationTemplate();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.ADD_CONFIGURATION_TEMPLATE, configurationTemplate, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void updateConfigurationTemplateWithInvalidParamsFailureTest() {
        ConfigurationTemplate configurationTemplate = new ConfigurationTemplate();
        Long id = 659L;
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.UPDATE_CONFIGURATION_TEMPLATE, configurationTemplate, Status.class, id);

        testExecutor.checkValidationFailure(status);

        status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.UPDATE_CONFIGURATION_TEMPLATE, FullfillmentValidationTest.validConfigurationTemplate, Status.class, id);

        testExecutor.checkValidationFailure(status, id);
    }

    @Test
    public void createReplicaWithInvalidParamsFailureTest() {
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.CREATE_REPLICA, replicaWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void removeReplicaWithInvalidParamsFailureTest() {
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.REMOVE_REPLICA, replicaWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void modifyReplicaPortWithInvalidParamsFailureTest() {
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_REPLICA_PORT, replicaWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

    @Test
    public void makeCollectionFullRebuildIndexWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        String serverName = "not-existing-server-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_INDEX, null, Status.class, collectionName, serverName);

        testExecutor.checkValidationFailure(status, collectionName, serverName);
    }

    @Test
    public void makeCollectionFullRebuildApplyWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        String serverName = "not-existing-server-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MAKE_COLLECTION_FULL_REBUILD_APPLY, null, Status.class, collectionName, serverName);

        testExecutor.checkValidationFailure(status, collectionName, serverName);
    }

    @Test
    public void deleteCollectionWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.DELETE_COLLECTION, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void deleteServerWithInvalidParamsFailureTest() {
        Long serverId = 867L;
        ResponseEntity<Status> response = REST_TEMPLATE.exchange(serverURI + CoordinatorConfigurationRestURIConstants.DELETE_SERVER, HttpMethod.DELETE, null, Status.class, serverId);

        testExecutor.checkValidationFailure(response.getBody(), serverId);
    }

    @Test
    public void deleteAdminProcessWithInvalidParamsFailureTest() {
        Long adminProcessId = 867L;
        ResponseEntity<Status> response = REST_TEMPLATE.exchange(serverURI + CoordinatorConfigurationRestURIConstants.DELETE_ADMIN_PROCESS, HttpMethod.DELETE, null, Status.class, adminProcessId);

        testExecutor.checkValidationFailure(response.getBody(), adminProcessId);
    }

    @Test
    public void rebuildCollectionProcessWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.REBUILD_COLLECTION, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void mergeCollectionWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void stopMergingWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.STOP_MERGING, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void deleteIndexDataWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.DELETE_FULL_INDEX_DATA, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void stopAllProcessesWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.STOP_ALL_PROCESSES, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void stopProcessWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Long replicaNumber = 678L;
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.STOP_PROCESS, null, Status.class, collectionName, replicaNumber);

        testExecutor.checkValidationFailure(status, collectionName, replicaNumber);
    }

    @Test
    public void startAllProcessesWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.START_ALL_PROCESSES, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void startProcessWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Long replicaNumber = 678L;
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.START_PROCESS, null, Status.class, collectionName, replicaNumber);

        testExecutor.checkValidationFailure(status, collectionName, replicaNumber);
    }

    @Test
    public void stopIndexingWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.STOP_INDEXING, null, Status.class, collectionName);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void deleteConfigurationTemplateWithInvalidParamsFailureTest() {
        Long configurationTemplateId = 678L;
        ResponseEntity<Status> response = REST_TEMPLATE.exchange(serverURI + CoordinatorConfigurationRestURIConstants.DELETE_CONFIGURATION_TEMPLATE, HttpMethod.DELETE, null, Status.class, configurationTemplateId);

        testExecutor.checkValidationFailure(response.getBody(), configurationTemplateId);
    }

    @Test
    public void disableSchedulingWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        String taskType = ScheduledTaskType.INDEXING_DELTA.toString();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.DISABLE_SCHEDULING, null, Status.class, collectionName, taskType);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void enableSchedulingWithInvalidParamsFailureTest() {
        String collectionName = "non-existent-collection-name";
        String taskType = ScheduledTaskType.INDEXING_DELTA.toString();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.ENABLE_SCHEDULING, null, Status.class, collectionName, taskType);

        testExecutor.checkValidationFailure(status, collectionName);
    }

    @Test
    public void addAdminProcessesWithInvalidParamsFailureTest() {
        ServerWrapper serverWrapper = new ServerWrapper();
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.ADD_ADMIN_PROCESSES, serverWrapper, Status.class);

        testExecutor.checkValidationFailure(status);
    }

/*    @Test
    public void getQueryHistoryTotalTimeWithInvalidParamsFailureTest() {

        // todo lnovikova реализовать валидацию для методов, возвращающих НЕ Status + сделать покрытие на методы getQueryHistoryTotalTime, getQueryHistoryResultCount, getQueryHistoryQueryCount, getQueryHistoryOffsetNotZeroCount
        SearchQueryHistorySearchParameters targetObject = new SearchQueryHistorySearchParameters();
        List list = REST_TEMPLATE.postForObject(serverURI + CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_TOTAL_TIME, targetObject, List.class);

    }*/
}
