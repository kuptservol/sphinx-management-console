package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 10.07.2015.
 */
public class StopSnippetFullRebuildExecutionTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void stopSnippetFullRebuildExecution() throws Throwable {
        logger.info("--- REBUILD SNIPPET CONFIGURATION ---");

        testExecutor.stopSnippetFullRebuildExecution(TEST_SNIPPET_COLLECTION_NAME);
    }
}