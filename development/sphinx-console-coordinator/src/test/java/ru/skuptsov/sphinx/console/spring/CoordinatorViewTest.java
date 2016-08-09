package ru.skuptsov.sphinx.console.spring;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;

public class CoordinatorViewTest {

    public static final String SERVER_URI = "http://localhost:8080/sphinx.console-coordinator";
    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Ignore
    @Test
    public void successfulTest() {
        Status status = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.TEST, Status.class);
        System.out.println(status.getCode());
        Assert.assertEquals(0, status.getCode());
    }

    @Ignore
    @Test
    public void serversTest() {
        Server[] servers = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.SERVERS, Server[].class);
        System.out.println(servers.length);
        Assert.assertEquals(3, servers.length);
    }

    @Ignore
    @Test
    public void processesTest() {
        SphinxProcess[] processes = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.PROCESSES, SphinxProcess[].class);
        System.out.println(processes.length);
        Assert.assertEquals(1, processes.length);
    }

    @Ignore
    @Test
    public void collectionTest() {
        Collection[] collections = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.COLLECTIONS, Collection[].class);
        System.out.println(collections.length);
        Assert.assertEquals(1, collections.length);
    }

    @Ignore
    @Test
    public void logTest() {
        ActivityLogSearchParameters parameters = new ActivityLogSearchParameters();
        parameters.setTaskStatus(TaskStatus.RUNNING);
        ActivityLog[] activityLog = REST_TEMPLATE.postForObject(SERVER_URI + CoordinatorViewRestURIConstants.LOG, parameters, ActivityLog[].class);
        Assert.assertEquals(1, activityLog.length);
    }

    @Ignore
    @Test
    public void scheduledTasksTest() {
        ScheduledTask[] scheduledTasks = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.SCHEDULED_TASKS, ScheduledTask[].class);
        Assert.assertEquals(1, scheduledTasks.length);
    }

    @Ignore
    @Test
    public void searchConfigurationTemplatesTest() {
        ConfigurationTemplate[] searchConfigurationTemplates = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.SEARCH_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(1, searchConfigurationTemplates.length);
    }

    @Ignore
    @Test
    public void configurationTemplatesTest() {
        ConfigurationTemplate[] configurationTemplates = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.CONFIGURATION_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertTrue(configurationTemplates.length > 0);
    }

    @Ignore
    @Test
    public void indexerConfigurationTemplatesTest() {
        ConfigurationTemplate[] configurationTemplates = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.INDEXER_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertEquals(1, configurationTemplates.length);
    }

    @Ignore
    @Test
    public void adminProcessTest() {
        AdminProcess adminProcess = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.ADMIN_PROCESS, AdminProcess.class, 1);
        Assert.assertNotNull(adminProcess);
    }

    @Ignore
    @Test
    public void configurationTest() {
        Configuration configuration = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.CONFIGURATION, Configuration.class, 1);
        Assert.assertNotNull(configuration);
    }

    @Ignore
    @Test
    public void taskStatusTest() {
        TaskStatus taskStatus = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.TASK_STATUS, TaskStatus.class, 1);
        Assert.assertNotNull(taskStatus);
    }

    @Ignore
    @Test
    public void processStatusTest() {
        ProcessStatus processStatus = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.PROCESS_STATUS, ProcessStatus.class, "collection1");
        Assert.assertEquals(ProcessStatus.SUCCESS, processStatus);
    }

    @Ignore
    @Test
    public void processStatusTest2() {
        ProcessStatus processStatus = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.PROCESS_STATUS, ProcessStatus.class, "mysqld2");
        Assert.assertEquals(ProcessStatus.FAILURE, processStatus);
    }

    @Ignore
    @Test
    public void getReplicasTest() {
        ListDataViewWrapper result = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.REPLICAS, ListDataViewWrapper.class, "collection1");
        Assert.assertEquals(result.getTotal().longValue(), 1);
    }

    @Ignore
    @Test
    public void getDeltasTest() {
        Delta[] result = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorViewRestURIConstants.DELTAS, Delta[].class, "collection1");
        Assert.assertEquals(result.length, 5);
    }
}
