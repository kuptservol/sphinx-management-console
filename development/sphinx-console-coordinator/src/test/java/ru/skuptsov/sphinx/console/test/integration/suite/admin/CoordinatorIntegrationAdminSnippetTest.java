package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.test.integration.tests.snippet.admin.*;

/**
 * Created by Developer on 06.07.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        CreateSnippetConfigurationAdminTest.class,
        MakeSnippetFullRebuildAdminTest.class,
        StopSnippetFullRebuildExecutionAdminTest.class,
        EditSnippetConfigurationAdminTest.class,
        RebuildSnippetsAdminTest.class,
        DeleteSnippetConfigurationAdminTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-snippet-context.xml"})
public class CoordinatorIntegrationAdminSnippetTest {
}
