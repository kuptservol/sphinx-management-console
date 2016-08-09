package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class CollectionWrapperTest extends FullfillmentValidationTest {

    @Test
    public void collectionWrapperFullfillmentTest(){
        CollectionWrapper collectionWrapper = new CollectionWrapper();
        String validationErrors;
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "searchConfigurationPort"));
        collectionWrapper.setSearchConfigurationPort(validPortWrapper);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "searchConfigurationPort"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "searchConfiguration"));
        collectionWrapper.setSearchConfiguration(validConfiguration);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "searchConfiguration"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexConfiguration"));
        collectionWrapper.setIndexConfiguration(validConfiguration);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "indexConfiguration"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "searchServer"));
        collectionWrapper.setSearchServer(validServer);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "searchServer"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexServer"));
        collectionWrapper.setIndexServer(validServer);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "indexServer"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "collection"));
        collectionWrapper.setCollection(validCollection);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "collection"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "cronSchedule"));
        collectionWrapper.setCronSchedule(validCronScheduleWrapper);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "cronSchedule"));

        Assert.assertNull(validationErrors);

        collectionWrapper.setMainCronSchedule(validCronScheduleWrapper);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

        collectionWrapper.setFullIndexingServer(validServer);
        validationErrors = getValidationError(collectionWrapper);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);
    }

}
