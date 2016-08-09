package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class AddDistributedCollection22Test extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void addDistributedCollection() throws Throwable {

        logger.info("--- CREATE DISTRIBUTED COLLECTION ---");
        DistributedCollectionWrapper collectionWrapper
                = testExecutor.buildDistributedCollectionWrapper(distributedCollectionName2, indexingAgentServer, collectionPort2, simpleCollectionName1, simpleCollectionName2);

        testExecutor.addDistributedCollection(collectionWrapper, CollectionType.DISTRIBUTED);
    }
}
