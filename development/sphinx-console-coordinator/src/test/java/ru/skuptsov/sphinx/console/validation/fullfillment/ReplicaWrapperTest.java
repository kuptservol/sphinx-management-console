package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ReplicaWrapperTest extends FullfillmentValidationTest {

    @Test
    public void validateObjectTest(){
        ReplicaWrapper targetObject = new ReplicaWrapper();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "collectionName", "");

        checkValidationErrorFixed(targetObject, "collectionName", "collection_name");

        checkValidationErrorForNotRequiredNotFixed(targetObject, "searchPort", 123456789);

        checkValidationErrorForNotRequiredFixed(targetObject, "searchPort", 123);

        checkValidationErrorForNotRequiredFixed(targetObject, "replicaNumber", 1l);

        checkValidationErrorForNotRequiredNotFixed(targetObject, "server", new Server());

        checkValidationErrorForNotRequiredFixed(targetObject, "server", validServer);

        checkValidationErrorNull(targetObject);

    }

}
