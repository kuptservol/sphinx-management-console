package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesNoChangeTask;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.util.Date;

public class ModifyDeltaMainCollectionAttributesNoChangeAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
//    @Ignore
    public void deltaMainModifyCollectionAttributesNoChangeTest() throws Throwable {
        String idField = ((FieldMapping)fieldMappings.toArray()[0]).getSourceField();
        String textField = ((FieldMapping)fieldMappings.toArray()[1]).getSourceField();
        String searchText =  textForSearchPrefix + " " + new Date().getTime() ;

        testExecutor.addSearchDataIntoDBTable(deltaMainDataSourceHost,
                                            deltaMainDataSourcePort,
                                            deltaMainDatasourceDB,
                                            deltaMainDataSourceUsername,
                                            deltaMainDataSourcePassword,
                                            deltaMainTable,
                                            textField,
                                            searchText);

        testExecutor.rebuildCollectionAdmin(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/rebuildCollection/rebuildCollection.xml");
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, searchText);

        testExecutor.modifyCollectionAttributesNoChange(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/modifyCollectionAttributesNoChange/modifyCollectionAttributesNoChange.xml",
                                                        deltaMainCollectionName,
                                                        searchAgentServer.getIp(),
                                                        replica1SearchPort,
                                                        ModifyCollectionAttributesNoChangeTask.TASK_NAME);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, searchText);
    }
}
