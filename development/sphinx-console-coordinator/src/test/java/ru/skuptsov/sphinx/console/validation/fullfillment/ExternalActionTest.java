package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ExternalAction;
import ru.skuptsov.sphinx.console.coordinator.model.ExternalActionType;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ExternalActionTest extends FullfillmentValidationTest {

    @Test
    public void externatActionTest(){
        ExternalAction externalAction = new ExternalAction();
        String validationErrors = null;
        validationErrors = getValidationError(externalAction);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "type"));
        externalAction.setType(ExternalActionType.SQL);
        validationErrors = getValidationError(externalAction);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "type"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "code"));
        externalAction.setCode("");
        validationErrors = getValidationError(externalAction);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "code"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "code"));
        externalAction.setCode("select * from servers");
        validationErrors = getValidationError(externalAction);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "code"));

/*        Assert.assertTrue(hasValidationErrorForField(validationErrors, "dataSource"));
        externalAction.setDataSource(validDataSource);
        validationErrors = getValidationError(externalAction);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "dataSource"));
*/
        Assert.assertNull(validationErrors);
    }

}
