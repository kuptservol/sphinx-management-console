package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ConfigurationFieldsTest extends FullfillmentValidationTest {

    @Test
    public void configurationFieldsTest(){
        ConfigurationFields configurationField = new ConfigurationFields();
        String validationErrors;
        validationErrors = getValidationError(configurationField);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldKey"));
        configurationField.setFieldKey("");
        validationErrors = getValidationError(configurationField);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldKey"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldKey"));
        configurationField.setFieldKey("field_key");
        validationErrors = getValidationError(configurationField);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "fieldKey"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValue"));
        configurationField.setFieldValue("");
        validationErrors = getValidationError(configurationField);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValue"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "fieldValue"));
        configurationField.setFieldValue("field_value");
        validationErrors = getValidationError(configurationField);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "fieldValue"));

        Assert.assertNull(validationErrors);

        configurationField.setConfiguration(validConfiguration);
        validationErrors = getValidationError(configurationField);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

    }

}
