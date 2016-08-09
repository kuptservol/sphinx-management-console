package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class DeleteDeltaMainRepairCollectionTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
//    @Ignore
    public void deleteDeltaMainCollectionDistributedServer() throws InterruptedException {
        logger.info("--- START DELETING DELTA MAIN DISTRIBUTED COLLECTION ONE SERVER ---.");
        testExecutor.deleteCollection(deltaMainCollectionName+REPAIR_SUFFIX, jdbcUrl, jdbcUsername, jdbcPassword, "root", searchingAgentServerRootPassword);
    }

}
