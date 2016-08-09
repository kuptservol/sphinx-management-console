package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class CreateReplicaDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void createReplica() throws Throwable {
        logger.info("--- CREATE DISTRIBUTED REPLICA ---");
        ReplicaWrapper replicaWrapper = testExecutor.buildDistributedReplicaWrapper(distributedCollectionName2, indexingAgentServer, newDistributedReplicaPort);
        testExecutor.createDistributedReplica(replicaWrapper);
    }
}
