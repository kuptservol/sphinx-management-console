package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class SphinxQLDeltaMainTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
    public void sphinxQLQueryTest() {
        testExecutor.getSphinxQLConsoleResult(searchAgentServer.getName(), replica1SearchPort, deltaMainCollectionName);
    }

}
