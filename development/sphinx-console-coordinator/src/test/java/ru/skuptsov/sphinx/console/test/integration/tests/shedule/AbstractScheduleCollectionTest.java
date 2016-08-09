package ru.skuptsov.sphinx.console.test.integration.tests.shedule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.TaskService;

import java.text.MessageFormat;

public abstract class AbstractScheduleCollectionTest {

    protected final static Logger logger = LoggerFactory.getLogger(AbstractScheduleCollectionTest.class);

    protected abstract String getCollectionName();
    protected abstract ScheduledTaskType getScheduledTaskType();
    protected abstract String getCroneBeforeTest();
    // max task duration in seconds
    private final int REPEAT_INTERVAL_MAX = 50;
    private int REPEAT_INTERVAL = 0;
    private String CHECK_CRONE;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    TaskService taskService;

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            logger.info("----------------------------STARTING TEST: " + description.getMethodName() + "--------------------------------");
        }
    };

    @Before
    public void initProperties() throws Exception {
        if(REPEAT_INTERVAL == 0) {
            REPEAT_INTERVAL = calculateRepeateInverval(getCollectionName(), getScheduledTaskType());
            logger.info("Repeat interval: " + REPEAT_INTERVAL);
            CHECK_CRONE = MessageFormat.format("/{0} * * * * ?", REPEAT_INTERVAL);
            logger.info("Check crone: " + CHECK_CRONE);
        }
    }

    @Test
//    @Ignore
    public void checkChangeEnabledSchedule() throws Throwable {

        // schedule must be running before this test, but we switched it off after add collection,
        // because scheduled tasks break tests from time to time
        // now, first switch on schedule, then run tests
        scheduleService.enableScheduleWithoutCheckFiringTasks(getCollectionName(), getScheduledTaskType());
        scheduleService.changeEnabledSchedule(getCollectionName(), CHECK_CRONE, getScheduledTaskType(), REPEAT_INTERVAL);

    }

    @Test
//    @Ignore
    public void checkDisableSchedule() throws Throwable {

        scheduleService.disableSchedule(getCollectionName(), getScheduledTaskType(), REPEAT_INTERVAL);

    }

    @Test
//    @Ignore
    public void checkChangeDisabledSchedule() throws Throwable {

        scheduleService.changeDisabledSchedule(getCollectionName(), CHECK_CRONE, getScheduledTaskType(), REPEAT_INTERVAL);

    }

    @Test
//    @Ignore
    public void checkEnableSchedule() throws Throwable {

        scheduleService.enableSchedule(getCollectionName(), getScheduledTaskType(), REPEAT_INTERVAL);

    }

    @Test
//    @Ignore
    public void scheduleBeforeTestRepair() throws Throwable {

        scheduleService.changeSchedule(getCollectionName(), getCroneBeforeTest(), getScheduledTaskType());

    }

    public int calculateRepeateInverval(String collectionName, ScheduledTaskType scheduledTaskType) {
        int result;
        TaskName taskName = scheduleService.getTaskName(scheduledTaskType);
        int maxTaskDuration = taskService.getMaxTaskDuration(collectionName, taskName);
        logger.info(MessageFormat.format("Max task duration for {0} task type {1} is {2}", collectionName, taskName, maxTaskDuration));
        /*прибавляем 5 секунд чтобы был запас и округляем до 10, чтобы было удобно анализировать в случае возникновения ошибки*/
        int calculatedRepeatInterval = ((maxTaskDuration + 5) / 10 + 1) * 10;
        logger.info("Calculated repeat interval: " + calculatedRepeatInterval);
        if (calculatedRepeatInterval > REPEAT_INTERVAL_MAX) {
            logger.warn(MessageFormat.format("Calculated repeat interval is too big {0} duration for {1} is too big {2} sec", getScheduledTaskType(), getCollectionName(), calculatedRepeatInterval));
            logger.warn(MessageFormat.format("Repeat interval set to max permissible {0}", REPEAT_INTERVAL_MAX));
            result = REPEAT_INTERVAL_MAX;
        } else {
            result = calculatedRepeatInterval;
        }
        return result;
    }
}
