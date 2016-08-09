package ru.skuptsov.sphinx.console.test.integration.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.ListDataViewWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.TaskDataViewWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.test.integration.tests.prepare.PrepareTest;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer on 03.02.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-context.xml"})
public class TemplateTest extends PrepareTest {

    @Autowired
    private TestChecker testChecker;

    @Before
    public void before () throws SQLException, ClassNotFoundException {
        String oldValue = searchingAgentServerIP;
        searchingAgentServerIP = indexingAgentServerIp;
        super.prepareForTest();
        super.checkIfSystemReadyForTest();
        super.createServerAndProcess();
        searchingAgentServerIP = oldValue;
    }

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Test
    @Ignore
    public void testServiceLog () throws InterruptedException, SQLException, ClassNotFoundException {
        TaskLogsSearchParameters taskLogsSearchParameters = new TaskLogsSearchParameters();
        taskLogsSearchParameters.setTaskUid("09103a3e-6360-4fc4-af1f-885526fa4e06");

//        ResponseEntity<ListDataViewWrapper> tasksLogResponse = REST_TEMPLATE.postForEntity("http://sphinx.console.dks.skuptsov.ru:8080/sphinx.console-coordinator/" + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
        ResponseEntity<ListDataViewWrapper> tasksLogResponse = REST_TEMPLATE.postForEntity("http://localhost:8080/sphinx.console-coordinator/" + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
        ListDataViewWrapper<List<TaskDataViewWrapper>> taskLogs = (ListDataViewWrapper<List<TaskDataViewWrapper>>) tasksLogResponse.getBody();
        for (int i = 0; i < taskLogs.getList().size(); i++) {

        }

        taskLogsSearchParameters = new TaskLogsSearchParameters();
        taskLogsSearchParameters.setTaskUid("09103a3e-6360-4fc4-af1f-885526fa4e06");

//        tasksLogResponse = REST_TEMPLATE.postForEntity("http://sphinx.console.dks.skuptsov.ru:8080/sphinx.console-coordinator/" + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
        tasksLogResponse = REST_TEMPLATE.postForEntity("http://localhost:8080/sphinx.console-coordinator/" + CoordinatorViewRestURIConstants.TASK_LOG, taskLogsSearchParameters, ListDataViewWrapper.class);
        taskLogs = (ListDataViewWrapper<List<TaskDataViewWrapper>>) tasksLogResponse.getBody();
        for (int i = 0; i < taskLogs.getList().size(); i++) {

        }
    }

    @Test
    public void checkSize() {
        for(int i = 0; i < 100; i++) {
            Long size = null;
            try {
                size = testExecutor.getCollectionSize("test_collection_delta_main_distributed_server_repair", "195.26.187.170", 9311);
            } catch (Throwable throwable) {
                logger.error("Error during get collection size", throwable);
            }
            logger.info("size: " + size);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
