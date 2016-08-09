package ru.skuptsov.sphinx.console.test.integration.tests.snippet.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 07.07.2015.
 */
public class MakeSnippetFullRebuildAdminTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void fullRebuildSnippetsConfiguration() throws Throwable {
        logger.info("--- FULL REBUILD SNIPPET CONFIGURATION ---");

        testExecutor.executeChangesetWithCheck(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/snippetTests/makeSnippetsFullRebuild/makeSnippetsFullRebuild.xml");
    }
}