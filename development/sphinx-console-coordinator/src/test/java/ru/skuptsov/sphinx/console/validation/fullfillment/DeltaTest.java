package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class DeltaTest extends FullfillmentValidationTest {

    @Test
    public void deltaTest(){
        Delta delta = new Delta();
        String validationErrors = null;
        validationErrors = getValidationError(delta);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "type"));
        delta.setType(DeltaType.DELTA);
        validationErrors = getValidationError(delta);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "type"));

        Assert.assertNull(validationErrors);

        delta.setExternalAction(validExternalAction);
        validationErrors = getValidationError(delta);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

        delta.setDeleteScheme(validDeleteScheme);
        validationErrors = getValidationError(delta);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

        delta.setCollection(validCollection);
        validationErrors = getValidationError(delta);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);
    }

}
