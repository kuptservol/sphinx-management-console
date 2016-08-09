package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.test.integration.tests.snippet.*;

@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

//        PrepareTest.class,
        AddPlainSnippetCollectionTest.class,
        CreateSnippetConfigurationTest.class,
        RebuildSnippetsTest.class,
        MakeSnippetFullRebuildTest.class,
        EditSnippetConfigurationTest.class,
        ScheduleSnippetTest.class,
        DeletePlainCollectionTest.class
//        DeleteSnippetConfigurationTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-snippet-context.xml"})
public class CoordinatorIntegrationSnippetTest {
}