package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class RemoveReplicaDeltaMainCollectionTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Удаления реплики"
     * Сценарий
     *  - Для коллекции test_collection_delta_main_distributed_server удаляется
     *  1 реплика на search-агенте
     *  Список проверок
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     *  - Файлы по данной реплике удалены с сервера
     *  - реплика удалена из базы
     */
    @Test
//    @Ignore
    public void removeReplicaTest() throws InterruptedException {
        logger.info("--- REMOVE REPLICA TEST ---");
        String collectionName = deltaMainCollectionName;

        logger.info("REMOVE REPLICA FROM SEARCHING SERVER");
        ReplicaWrapper replicaWrapper = testExecutor.findReplicaWrapperAnyBySearchPort(collectionName, searchingAgentServerIP, replica2SearchPort);
        Assert.assertNotNull("Replica on port: " + replica2SearchPort + " for collection: " + collectionName + " not found.", replicaWrapper);
        testExecutor.removeReplica(collectionName, replicaWrapper.getReplicaNumber(), searchingAgentServerIP, searchingAgentServerRootPassword);

        logger.info("REMOVE REPLICA FROM INDEXING SERVER");
        ReplicaWrapper replicaWrapper2 = testExecutor.findReplicaWrapperAnyBySearchPort(collectionName, indexingAgentServerIp, replica3SearchPort);
        Assert.assertNotNull("Replica on port: " + replica3SearchPort + " for collection: " + collectionName + " not found.", replicaWrapper2);
        testExecutor.removeReplica(collectionName, replicaWrapper2.getReplicaNumber(), indexingAgentServerIp, indexingAgentServerRootPassword);
    }

}
