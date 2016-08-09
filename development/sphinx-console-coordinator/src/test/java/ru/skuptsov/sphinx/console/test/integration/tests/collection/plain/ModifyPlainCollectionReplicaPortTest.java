package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.test.integration.service.TestChecker;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class ModifyPlainCollectionReplicaPortTest extends TestEnvironmentPlainCollectionHelper {

    @Autowired
    private TestChecker testChecker;

    @Test
    public void modifyReplicaPort () throws Throwable {
        logger.info("--- MODIFY REPLICA PORT ON PLAIN COLLECTION ONE SERVER ---");
        String collectionName = TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME;

        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        replicaWrapper.setCollectionName(collectionName);
        replicaWrapper.setReplicaNumber(1L);
        replicaWrapper.setSearchPort(replica1SearchPortNew);

        Status status = REST_TEMPLATE.postForObject(serverURI + CoordinatorConfigurationRestURIConstants.MODIFY_REPLICA_PORT, replicaWrapper, Status.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(replicaWrapper);

        testChecker.checkStatus(status);
        testChecker.checkStatus(status.getTaskUID());
        testChecker.checkCollectionSize(collectionName, indexingAgentServer.getIp(), replica1SearchPortNew);
    }
}
