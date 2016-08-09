package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;

import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class ModifyDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void modifyDistributedCollection() throws Throwable {
        logger.info("--- MODIFY DISTRIBUTED COLLECTION ---");
        DistributedCollectionWrapper collectionWrapper = testExecutor.getDistributedCollectionWrapper(distributedCollectionName1);


        testExecutor.modifyDistributedCollection(collectionWrapper, CollectionType.DISTRIBUTED);
    }
}