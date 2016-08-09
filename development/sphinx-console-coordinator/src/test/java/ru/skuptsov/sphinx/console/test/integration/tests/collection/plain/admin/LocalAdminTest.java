package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 29.04.2015.
 */
public class LocalAdminTest extends TestEnvironmentPlainCollectionHelper {
    @Test
//    @Ignore
    public void addPlainCollectionOneServer() throws Throwable {
        logger.info("--- CREATE PLAIN COLLECTION ONE SERVER ---");

        testExecutor.addCollection("development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\plainTests\\addPlainCollection\\addPlainCollection.xml",
                TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME,
                indexingAgentServer.getIp(),
                replica1SearchPort,
                CollectionType.SIMPLE);
    }

    @Test
//    @Ignore
    public void addPlainCollectionOneServer1() throws Throwable {
        logger.info("--- CREATE PLAIN COLLECTION ONE SERVER ---");

        testExecutor.addCollection("development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\plainTests\\addPlainCollection\\addPlainCollection.xml",
                TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME,
                indexingAgentServer.getIp(),
                replica1SearchPort,
                CollectionType.SIMPLE);
    }
}
