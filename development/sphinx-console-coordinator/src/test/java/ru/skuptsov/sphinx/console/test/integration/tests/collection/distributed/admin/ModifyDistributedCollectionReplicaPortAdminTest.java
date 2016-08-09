package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

/**
 * Created by Developer on 22.06.2015.
 */
public class ModifyDistributedCollectionReplicaPortAdminTest extends TestEnvironmentDistributedCollectionHelper {
    @Test
//    @Ignore
    public void modifyDistributedCollection() throws Throwable {
        logger.info("--- MODIFY DISTRIBUTED COLLECTION ---");

        testExecutor.executeChangeset(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/modifyDistributedCollectionReplicaPort/modifyDistributedCollectionReplicaPort.xml");
    }
}