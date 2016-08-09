package ru.skuptsov.sphinx.console.test.integration.service;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentHelper;


/**
 * Created by Andrey on 29.01.2015.
 */
@Service
public class TestChecker extends TestEnvironmentHelper {

    @Autowired
    private TestExecutor testExecutor;

    public void checkStatus(String taskUid) {
        testExecutor.checkStatus(taskUid);
    }

    public void checkStatus(String taskUid, String operationType, TaskStatus expectedTaskStatus) {
        testExecutor.checkStatus(taskUid, operationType, expectedTaskStatus);
    }

    public static void checkStatus(Status status) {
        Assert.assertEquals("Status returned non zero code: " + status.getCode(), 0, status.getCode());
    }

    public void checkCollectionSize(String collectionName,String ip, int port) throws Throwable {
        Long collectionSize = testExecutor.getCollectionSize(collectionName, ip, port);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Long infoCollectionSize = testExecutor.getCollectionInfo(collectionName).getCollectionSize();
        Assert.assertEquals("Size of the collection must be the same. Collection size: " + collectionSize + " Collection size from info: " + infoCollectionSize, collectionSize, infoCollectionSize);
    }
}
