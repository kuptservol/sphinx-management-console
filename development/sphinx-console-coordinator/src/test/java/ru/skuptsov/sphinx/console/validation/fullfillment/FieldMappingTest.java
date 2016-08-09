package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class FieldMappingTest extends FullfillmentValidationTest {

    @Test
    public void fieldMappingTest(){
        FieldMapping fieldMapping = new FieldMapping();
        String validationErrors;
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "sourceField"));
        fieldMapping.setSourceField("");
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "sourceField"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "sourceField"));
        fieldMapping.setSourceField("source_field");
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "sourceField"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexField"));
        fieldMapping.setIndexField("");
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexField"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexField"));
        fieldMapping.setIndexField("index_field");
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "indexField"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexFieldType"));
        fieldMapping.setIndexFieldType(IndexFieldType.SQL_FIELD);
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "indexFieldType"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "isId"));
        fieldMapping.setIsId(false);
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "isId"));

        Assert.assertNull(validationErrors);

        fieldMapping.setSourceFieldType("source_field_type");
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

        fieldMapping.setConfiguration(validConfiguration);
        validationErrors = getValidationError(fieldMapping);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

    }

}
