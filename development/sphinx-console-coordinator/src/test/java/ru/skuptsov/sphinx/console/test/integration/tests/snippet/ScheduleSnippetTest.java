package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;
import ru.skuptsov.sphinx.console.test.integration.tests.shedule.AbstractScheduleCollectionTest;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-plain-context.xml"})
public class ScheduleSnippetTest extends AbstractScheduleCollectionTest {

    @Override
    protected String getCollectionName() {
        return TestEnvironmentPlainCollectionHelper.TEST_SNIPPET_COLLECTION_NAME;
    }

    @Override
    protected String getCroneBeforeTest() {
        return "0 /10 * * * ?";
    }

    @Override
    protected ScheduledTaskType getScheduledTaskType() {
        return ScheduledTaskType.BUILD_SNIPPET;
    }

}
