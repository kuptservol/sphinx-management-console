package ru.skuptsov.sphinx.console.test.integration.tests.snippet.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 06.07.2015.
 */
public class EditSnippetConfigurationAdminTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void editSnippetConfiguration() throws Throwable {
        logger.info("--- EDIT SNIPPET CONFIGURATION ---");

        testExecutor.executeChangesetWithCheck(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/snippetTests/editSnippet/editSnippet.xml");
    }
}