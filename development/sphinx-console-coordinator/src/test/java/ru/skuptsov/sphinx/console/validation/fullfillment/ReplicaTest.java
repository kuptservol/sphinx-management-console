package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ReplicaTest extends FullfillmentValidationTest {

    @Test
    public void replicaTest(){
        Replica replica = new Replica();
        String validationErrors = null;
        validationErrors = getValidationError(replica);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "number"));
        replica.setNumber(1l);
        validationErrors = getValidationError(replica);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "number"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "collection"));
        replica.setCollection(validCollection);
        validationErrors = getValidationError(replica);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "collection"));

        Assert.assertNull(validationErrors);

        replica.setSearchProcess(validSphinxProcess);
        validationErrors = getValidationError(replica);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

    }

}
