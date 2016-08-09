package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ServerTest extends FullfillmentValidationTest {

    @Test
    public void serverFullfillmentTest(){
        String validationErrors = null;
        Server server = new Server();
        validationErrors = getValidationError(server);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "ip"));
        server.setIp("-1.1.2.3.5");
        validationErrors = getValidationError(server);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "ip"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "ip"));
        server.setIp("1.2.3.5");
        validationErrors = getValidationError(server);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "ip"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "name"));
        server.setName("server_name");
        validationErrors = getValidationError(server);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "name"));

        Assert.assertNull(validationErrors);
    }

}
