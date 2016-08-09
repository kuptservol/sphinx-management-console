package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.DeleteScheme;
import ru.skuptsov.sphinx.console.coordinator.model.DeleteSchemeType;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesNoChangeTask;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class DeleteSchemeTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
    public void deleteSchemeTest() throws Throwable {

        String attrName = ((FieldMapping)fieldMappings.toArray()[2]).getSourceField();

        testExecutor.checkSearchSuccessByCondition(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, attrName, "1");
        testExecutor.checkSearchSuccessByCondition(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, attrName, "1");
        testExecutor.checkSearchSuccessByCondition(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, attrName, "1");

        CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(deltaMainCollectionName);
        DeleteScheme deleteScheme = new DeleteScheme();
        deleteScheme.setType(DeleteSchemeType.BUSINESS_FIELD);
        deleteScheme.setFieldKey(attrName);
        deleteScheme.setFieldValueFrom("0");
        deleteScheme.setFieldValueTo("0");
        collectionWrapper.getCollection().getDelta().setDeleteScheme(deleteScheme);
        testExecutor.modifyCollectionAttributesNoChange(collectionWrapper, ModifyCollectionAttributesNoChangeTask.TASK_NAME);

        testExecutor.checkSearchFailsByCondition(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, attrName, "1");
        testExecutor.checkSearchFailsByCondition(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, attrName, "1");
        testExecutor.checkSearchFailsByCondition(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, attrName, "1");

    }

}
