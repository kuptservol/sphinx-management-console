package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.TaskWrapper;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class TaskWrapperTest extends FullfillmentValidationTest {

    @Test
    public void validateObjectTest(){
        TaskWrapper targetObject = new TaskWrapper();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "taskUID", "");

        checkValidationErrorFixed(targetObject, "taskUID", "sdf");

        checkValidationErrorNull(targetObject);

    }

}
