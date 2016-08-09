package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class AddReplicaDeltaMainCollectionAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Добавлении реплики"
     * Сценарий
     *  - Для коллекции test_collection_delta_main_distributed_server добавляется
     *  1 реплика на index-агенте
     *  1 реплика на search-агенте
     *  Список проверок
     *  - К реплике по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     *  - Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void addReplicaTest() throws Throwable {
        logger.info("--- ADD REPLICA TEST ---");

        logger.info("SEARCHING AGENT SERVER IP: " + searchAgentServer.getIp());
        logger.info("SEARCHING AGENT PORT: " + replica2SearchPort);
        testExecutor.createReplica(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/addReplicaDeltaMainCollection/addFirstReplicaDeltaMainCollection.xml",
                                   deltaMainCollectionName,
                                   searchAgentServer,
                                   replica2SearchPort);

        logger.info("INDEXING AGENT SERVER IP: " + indexingAgentServer.getIp());
        logger.info("INDEXING AGENT PORT: " + replica3SearchPort);
        testExecutor.createReplica(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/addReplicaDeltaMainCollection/addSecondReplicaDeltaMainCollection.xml",
                                   deltaMainCollectionName,
                                   indexingAgentServer,
                                   replica3SearchPort);
    }
}
