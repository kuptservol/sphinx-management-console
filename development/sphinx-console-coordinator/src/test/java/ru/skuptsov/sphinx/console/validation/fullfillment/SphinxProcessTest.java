package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class SphinxProcessTest extends FullfillmentValidationTest {

    @Test
    public void sphinxProcessTest(){
        SphinxProcess sphinxProcess = new SphinxProcess();
        String validationErrors = null;
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "server"));
        sphinxProcess.setServer(validServer);
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "server"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "collection"));
        sphinxProcess.setCollection(validCollection);
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "collection"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexName"));
        sphinxProcess.setIndexName("");
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexName"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "indexName"));
        sphinxProcess.setIndexName("index_name");
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "indexName"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "configuration"));
        sphinxProcess.setConfiguration(validConfiguration);
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "configuration"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "type"));
        sphinxProcess.setType(SphinxProcessType.INDEXING);
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "type"));

        Assert.assertNull(validationErrors);

        sphinxProcess.setReplica(validReplica);
        validationErrors = getValidationError(sphinxProcess);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

    }

}
