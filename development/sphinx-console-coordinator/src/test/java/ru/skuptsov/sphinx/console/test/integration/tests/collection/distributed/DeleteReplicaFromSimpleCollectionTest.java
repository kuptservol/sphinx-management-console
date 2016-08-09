package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class DeleteReplicaFromSimpleCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void deleteReplicaFromSimpleCollection() throws Throwable {

        logger.info("REMOVE REPLICA FROM SIMPLE COLLECTION OF DISTRIBUTED COLLECTION ");
        ReplicaWrapper replicaWrapper = testExecutor.findReplicaWrapperAnyBySearchPort(simpleCollectionName1, indexingAgentServerIp, replica2SearchPort);
        Assert.assertNotNull("Replica on port: " + replica2SearchPort + " for collection: " + simpleCollectionName1 + " not found.", replicaWrapper);
        testExecutor.deleteReplicaFromSimpleCollection(distributedCollectionName2,simpleCollectionName1, replicaWrapper.getReplicaNumber(),
                indexingAgentServerIp, searchingAgentServerRootPassword,
                jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);

    }
}
