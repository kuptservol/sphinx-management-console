package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class RemoveReplicaDeltaMainCollectionAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

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

        logger.info("REMOVE REPLICA FROM SEARCHING SERVER");
        testExecutor.removeReplica(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/removeReplicaDeltaMainCollection/removeFirstReplicaDeltaMainCollection.xml",
                                   deltaMainCollectionName,
                                   2l,
                                   searchingAgentServerIP,
                                   searchingAgentServerRootPassword);

        logger.info("REMOVE REPLICA FROM INDEXING SERVER");
        testExecutor.removeReplica(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/removeReplicaDeltaMainCollection/removeSecondReplicaDeltaMainCollection.xml",
                                   deltaMainCollectionName,
                                   3l,
                                   indexingAgentServerIp,
                                   indexingAgentServerRootPassword);
    }
}
