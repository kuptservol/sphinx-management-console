package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class AddPlainSnippetCollectionTest extends TestEnvironmentPlainCollectionHelper {

    @Test
//    @Ignore
    public void addPlainSnippetCollection() throws Throwable {
        logger.info("--- CREATE SNIPPET COLLECTION ONE SERVER ---");
        CollectionWrapper collectionWrapper = testExecutor.buildPlainCollectionWrapper("0 /10 * * * ?",
                indexingAgentServer, indexingAgentServer, TEST_SNIPPET_COLLECTION_NAME,
                testDataSourcePort, testDataSourceHost, testDatasourceDB, testDataSourceUsername,
                testDataSourcePassword, testDataSourceType, null, testDataSourceTable, testDataSourceTableColumnId,
                testDataSourceTableColumnField, snippetSearchPort, snippetDistributedSearchPort,
                testDataSourceTableColumnSqlField);

        testExecutor.addCollection(collectionWrapper, CollectionType.SIMPLE);
    }

}
