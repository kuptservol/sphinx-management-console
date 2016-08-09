package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CronScheduleWrapper;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class CronSheduleWrapperTest extends FullfillmentValidationTest {

    @Test
    public void cronSheduleWrapperTest(){
        CronScheduleWrapper cronScheduleWrapper = new CronScheduleWrapper();
        String validationErrors = null;
        validationErrors = getValidationError(cronScheduleWrapper);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "cronSchedule"));
        cronScheduleWrapper.setCronSchedule("/50 * * * *");
        validationErrors = getValidationError(cronScheduleWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "cronSchedule"));

        cronScheduleWrapper.setEnabled(false);

        Assert.assertNull(validationErrors);
    }

}
