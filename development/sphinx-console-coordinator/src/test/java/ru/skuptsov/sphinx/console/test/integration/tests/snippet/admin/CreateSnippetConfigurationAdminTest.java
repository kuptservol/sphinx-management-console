package ru.skuptsov.sphinx.console.test.integration.tests.snippet.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 03.07.2015.
 */
public class CreateSnippetConfigurationAdminTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void createSnippetConfiguration() throws Throwable {
        logger.info("--- CREATE SNIPPET CONFIGURATION ---");

        testExecutor.executeChangesetWithCheck(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/snippetTests/createSnippet/createSnippet.xml");
    }
}