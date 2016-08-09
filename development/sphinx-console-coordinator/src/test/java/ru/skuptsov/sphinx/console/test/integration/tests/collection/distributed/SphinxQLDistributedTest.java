package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class SphinxQLDistributedTest extends TestEnvironmentDistributedCollectionHelper {

    @Test
    public void sphinxQLQueryTest() {
        testExecutor.getSphinxQLConsoleResult(indexingAgentServer.getName(), collectionPort, distributedCollectionName1);
    }

}
