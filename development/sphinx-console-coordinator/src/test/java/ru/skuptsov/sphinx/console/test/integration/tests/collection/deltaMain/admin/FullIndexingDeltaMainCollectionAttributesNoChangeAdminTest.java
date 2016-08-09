package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.model.FullIndexingState;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.sql.SQLException;
import java.util.Date;

public class FullIndexingDeltaMainCollectionAttributesNoChangeAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

    private static String fullRebuildSearchText;

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * Фул-ребилд еще не выполнялся, поэтому статус должен быть NOT_RUNNING*/
    @Test
//    @Ignore
    public void repairDeltaMainCollectionDistributedServerfullIndexingStateNotRunningTest() {
        Assert.assertTrue(testExecutor.getCollectionInfo(deltaMainCollectionName+REPAIR_SUFFIX).getFullIndexingResult().getFullIndexingState() == FullIndexingState.NOT_RUNNING);
    }

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

        fullRebuildSearchText = textForSearchPrefix + " " + new Date().getTime() ;

        String idField = ((FieldMapping)fieldMappings.toArray()[0]).getSourceField();
        testExecutor.addSearchDataIntoDBTable(deltaMainDataSourceHost,
                deltaMainDataSourcePort,
                deltaMainDatasourceDB,
                deltaMainDataSourceUsername,
                deltaMainDataSourcePassword,
                deltaMainTable,
                fieldAdditionalForFullIndexing,
                fullRebuildSearchText);

        testExecutor.modifyCollectionAttributes(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/fullIndexingDeltaMainCollectionAttributesNoChange/modifyCollectionAttributes.xml");
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
        testExecutor.executeChangesetWithCheck(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/fullIndexingDeltaMainCollectionAttributesNoChange/makeCollectionFullRebuildApply.xml");
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, fieldAdditionalForFullIndexing, fullRebuildSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, fieldAdditionalForFullIndexing, fullRebuildSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, fieldAdditionalForFullIndexing, fullRebuildSearchText);
    }
}
