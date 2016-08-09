package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 06.07.2015.
 */
public class DeleteSnippetConfigurationTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void deleteSnippetConfiguration() throws Throwable {
        logger.info("--- DELETE SNIPPET CONFIGURATION ---");

        testExecutor.deleteSnippet(TEST_SNIPPET_COLLECTION_NAME);
    }
}