package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class AddDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void addDistributedCollection() throws Throwable {
        logger.info("--- CREATE DISTRIBUTED COLLECTION ---");
        DistributedCollectionWrapper collectionWrapper = testExecutor.buildDistributedCollectionWrapper(distributedCollectionName1, indexingAgentServer, collectionPort, simpleCollectionName3);

        testExecutor.addDistributedCollection(collectionWrapper, CollectionType.DISTRIBUTED);
    }
}
