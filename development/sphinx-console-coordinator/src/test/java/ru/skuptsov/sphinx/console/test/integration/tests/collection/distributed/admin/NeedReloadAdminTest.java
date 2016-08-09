package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

/**
 ¦Проверка проставления флага need_reload для распр коллекций при удалении нод/коллекции, редактировании нод/коллекции,
 создании нод в коллекциях, которые участвуют в распр поиске
 */
public class NeedReloadAdminTest extends TestEnvironmentDistributedCollectionHelper {
    @Test
//    @Ignore
    public void needReloadTest() throws Throwable {
        logger.info("--- NEED RELOAD TEST ---");

        //create replica
        testExecutor.createSimpleCollectionReplicaInDistributedCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/needReload/createSimpleReplica.xml",
                distributedCollectionName2, simpleCollectionName2, indexingAgentServer,
                collection2Replica3SearchPort, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
        testExecutor.reloadDistributedCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/needReload/reloadDistributedCollection.xml",
                PROJECT_PATH + adminTestPropertiesPath, distributedCollectionName2, Boolean.FALSE,
                jdbcUrl, jdbcUsername, jdbcPassword);

        //modify replica
        testExecutor.executeChangeset(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/needReload/modifySimpleReplica.xml");
        testExecutor.checkNeedReloadCollection(distributedCollectionName2, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
        testExecutor.reloadDistributedCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/needReload/reloadDistributedCollection.xml",
                PROJECT_PATH + adminTestPropertiesPath, distributedCollectionName2, Boolean.FALSE,
                jdbcUrl, jdbcUsername, jdbcPassword);

        //remove replica
        testExecutor.executeChangeset(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/distributedCollectionTests/needReload/removeSimpleReplica.xml");
        testExecutor.checkNeedReloadCollection(distributedCollectionName2, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
//        testExecutor.reloadDistributedCollection("development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\distributedCollectionTests\\needReload\\reloadDistributedCollection.xml",
//                TEST_COLLECTION_DISTRIBUTED_NAME_2, Boolean.FALSE,
//                jdbcUrl, jdbcUsername, jdbcPassword);
    }
}