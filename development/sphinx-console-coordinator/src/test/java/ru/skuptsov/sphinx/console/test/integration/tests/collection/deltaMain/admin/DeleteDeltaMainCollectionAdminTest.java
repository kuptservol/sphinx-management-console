package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class DeleteDeltaMainCollectionAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
//    @Ignore
    public void deleteDeltaMainCollectionDistributedServer() throws InterruptedException {
        logger.info("--- START DELETING DELTA MAIN DISTRIBUTED COLLECTION ONE SERVER ---.");
        testExecutor.deleteCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/deleteCollection/deleteCollection.xml",
                                      deltaMainCollectionName,
                                      jdbcUrl,
                                      jdbcUsername,
                                      jdbcPassword,
                                      "root",
                                      searchingAgentServerRootPassword);
    }
}
