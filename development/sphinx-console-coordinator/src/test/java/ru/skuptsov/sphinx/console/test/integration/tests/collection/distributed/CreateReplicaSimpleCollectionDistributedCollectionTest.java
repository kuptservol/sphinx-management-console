package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class CreateReplicaSimpleCollectionDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void createReplica() throws Throwable {
        logger.info("--- CREATE REPLICA SIMPLE COLLECTION OF DISTRIBUTED COLLECTION ---");
        testExecutor.createReplicaSimpleCollectionDistributedCollection(distributedCollectionName2, simpleCollectionName1, indexingAgentServer,
                replica3SearchPort, replica3DistributedSearchPort, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
    }
}