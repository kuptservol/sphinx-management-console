package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.DeleteScheme;
import ru.skuptsov.sphinx.console.coordinator.model.DeleteSchemeType;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class DeleteSchemeTest extends FullfillmentValidationTest {

    @Test
    public void deleteSchemeTest(){
        DeleteScheme deleteScheme = new DeleteScheme();
        String validationErrors = null;
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "type"));
        deleteScheme.setType(DeleteSchemeType.BUSINESS_FIELD);
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "type"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldKey"));
        deleteScheme.setFieldKey("");
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldKey"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldKey"));
        deleteScheme.setFieldKey("field_key");
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "fieldKey"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValueFrom"));
        deleteScheme.setFieldValueFrom("");
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValueFrom"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValueFrom"));
        deleteScheme.setFieldValueFrom("1");
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "fieldValueFrom"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValueTo"));
        deleteScheme.setFieldValueTo("");
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValueTo"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValueTo"));
        deleteScheme.setFieldValueTo("3");
        validationErrors = getValidationError(deleteScheme);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "fieldValueTo"));

        Assert.assertNull(validationErrors);
    }

}
