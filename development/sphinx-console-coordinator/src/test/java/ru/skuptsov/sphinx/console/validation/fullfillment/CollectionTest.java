package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class CollectionTest extends FullfillmentValidationTest {

    @Test
    public void collectionTest(){
        Collection collection = new Collection();
        String validationErrors = null;
        validationErrors = getValidationError(collection);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "name"));
        collection.setName("");
        validationErrors = getValidationError(collection);
        logger.info(validationErrors);
        Assert.assertTrue(hasValidationErrorForField(validationErrors, "name"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "name"));
        collection.setName("collection_name");
        validationErrors = getValidationError(collection);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "name"));

        Assert.assertTrue(hasValidationErrorForField(validationErrors, "type"));
        collection.setType(CollectionType.MAIN_DELTA);
        validationErrors = getValidationError(collection);
        logger.info(validationErrors);
        Assert.assertFalse(hasValidationErrorForField(validationErrors, "type"));

        checkValidationErrorForNotRequiredNotFixed(collection, "delta", new Delta());

        checkValidationErrorForNotRequiredFixed(collection, "delta", validDelta);

        Set<Replica> replicas = new HashSet<Replica>();
        replicas.add(validReplica);
        collection.setReplicas(replicas);
        validationErrors = getValidationError(collection);
        logger.info(validationErrors);
        Assert.assertNull(validationErrors);

    }

}
