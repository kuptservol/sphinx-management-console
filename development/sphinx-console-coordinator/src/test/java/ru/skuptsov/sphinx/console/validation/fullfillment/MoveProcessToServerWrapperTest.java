package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class MoveProcessToServerWrapperTest extends FullfillmentValidationTest {

    @Test
    public void validateObjectTest(){
        MoveProcessToServerWrapper targetObject = new MoveProcessToServerWrapper();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "newServer", new Server());

        checkValidationErrorFixed(targetObject, "newServer", validServer);

        checkValidationErrorNotFixed(targetObject, "searchSphinxProcess", new SphinxProcess());

        checkValidationErrorFixed(targetObject, "searchSphinxProcess", validSphinxProcess);

        checkValidationErrorNull(targetObject);
    }

}
