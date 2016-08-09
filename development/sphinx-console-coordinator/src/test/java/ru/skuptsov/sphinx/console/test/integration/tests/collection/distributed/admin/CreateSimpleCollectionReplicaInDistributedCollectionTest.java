package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

/**
 * Created by Developer on 19.06.2015.
 */
public class CreateSimpleCollectionReplicaInDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {
    @Test
//    @Ignore
    public void createReplicaDistributedCollection() throws Throwable {
        logger.info("--- CREATE SIMPLE COLLECTION REPLICA IN DISTRIBUTED COLLECTION ---");

        testExecutor.createSimpleCollectionReplicaInDistributedCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/createSimpleCollectionReplicaInDistributedCollection/createSimpleCollectionReplicaInDistributedCollection.xml",
                distributedCollectionName2, simpleCollectionName1, searchAgentServer,
                replica3SearchPort, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);

        testExecutor.reloadDistributedCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/createSimpleCollectionReplicaInDistributedCollection/reloadDistributedCollection.xml",
                PROJECT_PATH + adminTestPropertiesPath, distributedCollectionName2, Boolean.FALSE,
                jdbcUrl, jdbcUsername, jdbcPassword);
    }
}
