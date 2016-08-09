package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 03.07.2015.
 */
public class CreateSnippetConfigurationTest extends TestEnvironmentPlainCollectionHelper {

    @Autowired
    ScheduleService scheduleService;

    @Test
    public void createSnippetConfiguration() throws Throwable {
        logger.info("--- CREATE SNIPPET CONFIGURATION ---");

        testExecutor.createSnippet(testExecutor.buildSnippetConfigurationWrapper(TEST_SNIPPET_COLLECTION_NAME, "0 /10 * * * ?"));
        scheduleService.disableScheduleWithoutCheckFiringTasks(TEST_SNIPPET_COLLECTION_NAME, ScheduledTaskType.BUILD_SNIPPET);
    }
}