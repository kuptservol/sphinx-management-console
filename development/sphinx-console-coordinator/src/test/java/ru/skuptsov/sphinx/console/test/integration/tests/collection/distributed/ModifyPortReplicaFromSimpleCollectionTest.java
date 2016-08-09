package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class ModifyPortReplicaFromSimpleCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void modifyPortReplicaFromSimpleCollection() throws Throwable {

        logger.info("MODIFY PORT REPLICA FROM SIMPLE COLLECTION OF DISTRIBUTED COLLECTION ");
        ReplicaWrapper replicaWrapper = testExecutor.findReplicaWrapperAnyBySearchPort(simpleCollectionName2, indexingAgentServerIp, collection2Replica1SearchPort);
        Assert.assertNotNull("Replica on port: " + collection2Replica1SearchPort + " for collection: " + simpleCollectionName2 + " not found.", replicaWrapper);
        replicaWrapper.setDistributedPort(collection2Replica1DistributedSearchPortNew);
        testExecutor.modifyPortReplicaFromSimpleCollection(distributedCollectionName2, replicaWrapper, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);

    }
}
