package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

/**
 * Created by Developer on 19.06.2015.
 */
public class CreateReplicaDistributedCollectionAdminTest extends TestEnvironmentDistributedCollectionHelper {
    @Test
//    @Ignore
    public void createReplicaDistributedCollection() throws Throwable {
        logger.info("--- CREATE DISTRIBUTED COLLECTION REPLICA ---");

        testExecutor.executeChangeset(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/createReplicaDistributedCollection/createReplicaDistributedCollection.xml");
    }
}
