package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.test.integration.tests.shedule.AbstractScheduleCollectionTest;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-delta-main-context.xml"})
public class ScheduleDeltaMainCollectionDeltaTest extends AbstractScheduleCollectionTest {

    @Value("${delta.main.collection.name}")
    public String deltaMainCollectionName;

    @Value("${cron.delta}")
    public String cronDelta;

    @Override
    protected String getCollectionName() {
        return deltaMainCollectionName;
    }

    @Override
    protected String getCroneBeforeTest() {
        return cronDelta;
    }

    @Override
    protected ScheduledTaskType getScheduledTaskType() {
        return ScheduledTaskType.INDEXING_DELTA;
    }

}
