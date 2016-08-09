package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class DataSourceTest extends FullfillmentValidationTest {

    @Test
    public void dataSourceFullfillmentTest(){
        String validationErrors;
        DataSource dataSource = new DataSource();
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "type"));
        dataSource.setType(DataSourceType.PGSQL);
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "type"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "host"));
        dataSource.setHost("");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "host"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "host"));
        dataSource.setHost("localhost");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "host"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "port"));
        dataSource.setPort(12345678);
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "port"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "port"));
        dataSource.setPort(1234);
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "port"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "user"));
        dataSource.setUser("");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "user"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "user"));
        dataSource.setUser("user");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "user"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "password"));
        dataSource.setPassword("");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "password"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "password"));
        dataSource.setPassword("password");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "password"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "sqlDb"));
        dataSource.setSqlDb("");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "sqlDb"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "sqlDb"));
        dataSource.setSqlDb("db_name");
        validationErrors = getValidationError(dataSource);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "sqlDb"));

        Assert.assertNull(validationErrors);
    }

}
