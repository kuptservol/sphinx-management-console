package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class AdminProcessTest extends FullfillmentValidationTest {

    @Test
    public void adminProcessTest(){
        AdminProcess targetObject = new AdminProcess();

        checkInitErrors(targetObject);

        checkValidationErrorFixed(targetObject, "port", 123);

        checkValidationErrorFixed(targetObject, "type", ProcessType.COORDINATOR);

        checkValidationErrorNotFixed(targetObject, "server", new Server());

        checkValidationErrorFixed(targetObject, "server", validServer);

        checkValidationErrorNull(targetObject);
    }

}
