package ru.skuptsov.sphinx.console.test.integration.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionInfoWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.text.MessageFormat;

/**
 * Created by Andrey on 16.01.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-context.xml"})
public class TestExecutorTest {

    private static Logger logger= LoggerFactory.getLogger(TestExecutorTest.class);

    @Autowired
    TestExecutor testExecutor;

    @Ignore
    @Test
    public void checkStatus() {
        testExecutor.checkStatus("3e4f3b12-bced-44cf-bf62-aa48100404ba");
    }

    //@Ignore
    @Test
    public void checkSize() throws Throwable {
        String ip = "195.26.187.155";
        int collectionPort = 9306;
        String collectionName = TestEnvironmentPlainCollectionHelper.TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME;
        final String SEARCH_TEMPLATE = "SELECT * FROM {0} where match(\''@{1} {2}'\') limit 0,0;";
        String fieldName = "text_data";
        String searchText = "текст для поиска после пересборки коллекции с изменением параметров";
        //String query = "select count(*) from test_collection_delta_main_distributed_server_repair_delta";
        String query = MessageFormat.format(SEARCH_TEMPLATE, collectionName, fieldName, searchText);
        Long rowCount = testExecutor.getRowCountForCollectionQuery(query, ip, collectionPort);
        //for(int i = 0; i < 100; i++) {
        //    size = testExecutor.getCollectionSize("test_collection_delta_main_distributed_server_repair", "195.26.187.170", 9311);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        logger.info("row count " + rowCount);
    }

    //@Ignore
    @Test
    public void getCollectionInfo() {
        CollectionInfoWrapper collectionInfoWrapper = testExecutor.getCollectionInfo("test_collection_simple_one_server");
        logger.info("collection size: " + collectionInfoWrapper.getCollectionSize());
    }

    @Ignore
    @Test
    public void findReplicaWrapper() {
        ReplicaWrapper replicaWrapper = testExecutor.findReplicaWrapper("test_collection_simple_one_server", 2L);
        logger.info("replica number: " + replicaWrapper.getReplicaNumber());
    }

    @Ignore
    @Test
    public void findReplicaWrapperAnyBySearchPort() {
        ReplicaWrapper replicaWrapper = testExecutor.findReplicaWrapperAnyBySearchPort("test_collection_simple_one_server", "192.168.187.201", 7777);
        logger.info("replica number: " + replicaWrapper.getReplicaNumber());
    }

    @Ignore
    @Test
    public void isDirectoryExists() {
        String processName = "test_collection_simple_one_server" + "_" + 2L;
        String collectionRootDirectory = "/opt/sphinx.console/sphinx/";
        String path = collectionRootDirectory + "binlog/searching/" + processName;
        boolean exists = testExecutor.isPathExists("192.168.187.201", 22, "root", "se@rchw1z", path);
        logger.info("RESULT: " + exists);
    }

    @Ignore
    @Test
    public void killAllSearchdProcesses() {
        testExecutor.killallSearchdProcesses("192.168.187.201", 22, "root", "se@rchw1z");
        testExecutor.killallSearchdProcesses("192.168.187.200", 22, "root", "se@rchw1z");
    }
}
