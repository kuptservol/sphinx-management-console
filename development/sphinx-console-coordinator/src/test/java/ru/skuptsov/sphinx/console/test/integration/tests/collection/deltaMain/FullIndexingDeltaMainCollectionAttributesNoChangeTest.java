package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.sql.SQLException;
import java.util.Date;

public class FullIndexingDeltaMainCollectionAttributesNoChangeTest extends TestEnvironmentDeltaMainCollectionHelper {

    private static String fullRebuildSearchText;

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * Фул-ребилд еще не выполнялся, поэтому статус должен быть NOT_RUNNING*/
    @Test
//    @Ignore
    public void deltaMainCollectionfullIndexingStateNotRunningTest() {
        testExecutor.fullIndexingStateNotRunning(deltaMainCollectionName);
    }

    @Test
//    @Ignore
    public void deltaMainCollectionFullRebuildIndexTest() throws InterruptedException, SQLException, ClassNotFoundException {
        logger.info("--- MODIFY DELTA-MAIN COLLECTION ATTRIBUTES ---");
        CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(deltaMainCollectionName);
        collectionWrapper.setFullIndexingServer(indexingAgentServer);


        fullRebuildSearchText =  textForSearchPrefix + " " + new Date().getTime() ;

        String idField = ((FieldMapping)fieldMappings.toArray()[0]).getSourceField();
        testExecutor.addSearchDataIntoDBTable(deltaMainDataSourceHost,
                deltaMainDataSourcePort,
                deltaMainDatasourceDB,
                deltaMainDataSourceUsername,
                deltaMainDataSourcePassword,
                deltaMainTable,
                fieldAdditionalForFullIndexing,
                fullRebuildSearchText);

        testExecutor.modifyCollectionAttributes(collectionWrapper, fieldAdditionalForFullIndexing);
    }

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * После удачного завершения теста deltaMainCollectionFullRebuildIndexTest, статус должен быть READY_FOR_APPLY*/
    @Test
//    @Ignore
    public void deltaMainFullIndexingStateReadyForUpdateTest() {
        testExecutor.fullIndexingStateReadyForApply(deltaMainCollectionName);
    }

    @Test
//    @Ignore
    public void deltaMainFullRebuildApplyTest() throws Throwable {
        String serverName = null;
        // for single execution of test without executing all previous methods (in this case required variables will be uninitialized)
        if (indexingAgentServer == null) {
            serverName = coordinatorServerIp.equals(indexingAgentServerIp) ? COORDINATOR_SERVER_NAME : INDEXING_AGENT_SERVER_NAME;
        } else {
            serverName = indexingAgentServer.getName();
        }

        testExecutor.makeCollectionFullRebuildApply(deltaMainCollectionName, serverName);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, fieldAdditionalForFullIndexing, fullRebuildSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, fieldAdditionalForFullIndexing, fullRebuildSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, fieldAdditionalForFullIndexing, fullRebuildSearchText);
    }

}
