package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class ModifyPortReplicaDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void modifyPortReplicaDistributedCollection() throws Throwable {
        logger.info("MODIFY PORT REPLICA DISTRIBUTED COLLECTION ");
        ReplicaWrapper replicaWrapper = testExecutor.findReplicaWrapperAnyBySearchPort(distributedCollectionName2, indexingAgentServerIp, collectionPort2);
        Assert.assertNotNull("Replica on port: " + collection2Replica1SearchPort + " for collection: " + distributedCollectionName2 + " not found.", replicaWrapper);
        replicaWrapper.setSearchPort(modifiedDistributedReplicaPort);
        testExecutor.modifyPortReplicaDistributedCollection(replicaWrapper);
    }
}
