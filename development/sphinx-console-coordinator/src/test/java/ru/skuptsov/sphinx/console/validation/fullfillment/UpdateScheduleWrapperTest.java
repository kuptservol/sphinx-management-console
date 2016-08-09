package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.UpdateScheduleWrapper;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class UpdateScheduleWrapperTest extends FullfillmentValidationTest {

    @Test
    public void UpdateScheduleWrapperTest(){
        UpdateScheduleWrapper targetObject = new UpdateScheduleWrapper();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "collectionName", "");

        checkValidationErrorFixed(targetObject, "collectionName", "collection_name");

        checkValidationErrorFixed(targetObject, "cronExpression", "0 /10 * * * ?");

        checkValidationErrorFixed(targetObject, "type", ScheduledTaskType.INDEXING_DELTA);

        checkValidationErrorNull(targetObject);

    }

}
