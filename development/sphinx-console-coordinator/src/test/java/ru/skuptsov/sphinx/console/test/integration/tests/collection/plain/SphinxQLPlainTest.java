package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class SphinxQLPlainTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void sphinxQLQueryTest() {
        testExecutor.getSphinxQLConsoleResult(indexingAgentServer.getName(), replica1SearchPort, TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
    }

}
