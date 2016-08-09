package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class RepeatedTaskTest extends TestEnvironmentDeltaMainCollectionHelper {
    @Test
//    @Ignore
    public void repeatedTaskNotFiredTest() throws Throwable {
        logger.info("Merge collection " + deltaMainCollectionName);
        Status statusFirstRebuild = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, null, Status.class, deltaMainCollectionName);
        logger.info("Request status: " + statusFirstRebuild);
        Assert.assertEquals(0, statusFirstRebuild.getCode());
        logger.info("Merge collection " + deltaMainCollectionName);
        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MERGE_COLLECTION, null, Status.class, deltaMainCollectionName);
        logger.info("Request status: " + status);
        logger.info("Expect to get status " + Status.StatusCode.FAILURE_REPEATED_TASK.getCode());
        Assert.assertEquals(Status.StatusCode.FAILURE_REPEATED_TASK.getCode(), status.getCode());
        // Проверяем что первый ребилд завершился успешно.
        // Нужно для теста прохождения двух последовательных ребилдов,
        // второй из которых стартует после успешного завершения первого
        testExecutor.checkStatus(statusFirstRebuild.getTaskUID());
    }

    @Test
//    @Ignore
    public void rebuildTwoTimesTest() throws Throwable {
        testExecutor.rebuildCollection(deltaMainCollectionName);
        testExecutor.rebuildCollection(deltaMainCollectionName);
    }
}
