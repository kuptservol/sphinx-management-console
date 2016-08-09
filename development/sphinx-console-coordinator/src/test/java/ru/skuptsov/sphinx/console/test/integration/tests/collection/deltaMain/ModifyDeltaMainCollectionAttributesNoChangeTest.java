package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesNoChangeTask;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.util.Date;

public class ModifyDeltaMainCollectionAttributesNoChangeTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
//    @Ignore
    public void deltaMainModifyCollectionAttributesNoChangeTest() throws Throwable {

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

        testExecutor.rebuildCollection(deltaMainCollectionName);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, searchText);

        CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(deltaMainCollectionName);
        testExecutor.modifyCollectionAttributesNoChange(collectionWrapper, ModifyCollectionAttributesNoChangeTask.TASK_NAME);

        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, searchText);

    }

}
