package ru.skuptsov.sphinx.console.test.integration.tests.snippet.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 10.07.2015.
 */
public class StopSnippetFullRebuildExecutionAdminTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void rebuildSnippetsConfiguration() throws Throwable {
        logger.info("--- REBUILD SNIPPET CONFIGURATION ---");

        testExecutor.executeChangesetWithCheck(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/snippetTests/stopSnippetFullRebuildExecution/stopSnippetFullRebuildExecution.xml");
    }
}