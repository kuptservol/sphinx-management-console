package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class SearchConfigurationPortWrapperTest extends FullfillmentValidationTest {

    @Test
    public void searchConfigurationPortWrapperTest(){
        String validationErrors;
        SearchConfigurationPortWrapper portWrapper = new SearchConfigurationPortWrapper();
        validationErrors = getValidationError(portWrapper);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "searchConfigurationPort"));
        portWrapper.setSearchConfigurationPort(999999999);
        validationErrors = getValidationError(portWrapper);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "searchConfigurationPort"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "searchConfigurationPort"));
        portWrapper.setSearchConfigurationPort(1234);
        validationErrors = getValidationError(portWrapper);
        logger.info(validationErrors);

        Assert.assertNull(validationErrors);
    }

}
