package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfigurationWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 06.07.2015.
 */
public class EditSnippetConfigurationTest extends TestEnvironmentPlainCollectionHelper {

    @Autowired
    ScheduleService scheduleService;

    @Test
    public void editSnippetConfiguration() throws Throwable {
        logger.info("--- EDIT SNIPPET CONFIGURATION ---");

        SnippetConfigurationWrapper snippetConfigurationWrapper = testExecutor.getSnippetConfigurationWrapper(TEST_SNIPPET_COLLECTION_NAME);
        snippetConfigurationWrapper.getSnippetConfiguration().setPreQuery("select id, value from test.main");
        snippetConfigurationWrapper.getSnippetConfiguration().setPostQuery("select id, value from test.delta");

        testExecutor.editSnippet(snippetConfigurationWrapper);
        scheduleService.disableScheduleWithoutCheckFiringTasks(TEST_SNIPPET_COLLECTION_NAME, ScheduledTaskType.BUILD_SNIPPET);
    }
}