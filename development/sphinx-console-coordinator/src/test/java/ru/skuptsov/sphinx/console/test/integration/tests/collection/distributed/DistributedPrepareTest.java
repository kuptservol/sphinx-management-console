package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class DistributedPrepareTest extends TestEnvironmentDistributedCollectionHelper {
	  @Test
//    @Ignore
    public void createSimpleCollections() throws Throwable {
          logger.info("--- CREATE PLAIN COLLECTION ONE ---");
          CollectionWrapper collectionWrapper1 = testExecutor.buildPlainCollectionWrapper("0 /10 * * * ?",
                  indexingAgentServer, indexingAgentServer, simpleCollectionName1,
                  testDataSourcePort, testDataSourceHost, testDatasourceDB, testDataSourceUsername,
                  testDataSourcePassword, testDataSourceType, null, testDataSourceTable, testDataSourceTableColumnId,
                  testDataSourceTableColumnField, replica1SearchPort, replica1DistributedSearchPort,
                  testDataSourceTableColumnSqlField);

          testExecutor.addCollection(collectionWrapper1, CollectionType.SIMPLE);
          logger.info("--- CREATE REPLICA FOR COLLECTION ONE ---");
          testExecutor.createReplica(simpleCollectionName1, indexingAgentServer, replica2SearchPort, replica2DistributedSearchPort);

          logger.info("--- CREATE PLAIN COLLECTION TWO ---");
          CollectionWrapper collectionWrapper2 = testExecutor.buildPlainCollectionWrapper("0 /10 * * * ?",
                  indexingAgentServer, indexingAgentServer, simpleCollectionName2,
                  testDataSourcePort, testDataSourceHost, testDatasourceDB, testDataSourceUsername,
                  testDataSourcePassword, testDataSourceType, null, testDataSourceTable, testDataSourceTableColumnId,
                  testDataSourceTableColumnField, collection2Replica1SearchPort, collection2Replica1DistributedSearchPort,
                  testDataSourceTableColumnSqlField);

          testExecutor.addCollection(collectionWrapper2, CollectionType.SIMPLE);
          logger.info("--- CREATE REPLICA FOR COLLECTION TWO ---");
          testExecutor.createReplica(simpleCollectionName2, indexingAgentServer, collection2Replica2SearchPort, collection2Replica2DistributedSearchPort);

          logger.info("--- CREATE PLAIN COLLECTION THREE ---");
          CollectionWrapper collectionWrapper3 = testExecutor.buildPlainCollectionWrapper("0 /10 * * * ?",
                  indexingAgentServer, indexingAgentServer, simpleCollectionName3,
                  testDataSourcePort, testDataSourceHost, testDatasourceDB, testDataSourceUsername,
                  testDataSourcePassword, testDataSourceType, null, testDataSourceTable, testDataSourceTableColumnId,
                  testDataSourceTableColumnField, collection3Replica1SearchPort, collection3Replica1DistributedSearchPort,
                  testDataSourceTableColumnSqlField);

          testExecutor.addCollection(collectionWrapper3, CollectionType.SIMPLE);
          logger.info("--- CREATE REPLICA FOR COLLECTION THREE ---");
          testExecutor.createReplica(simpleCollectionName3, indexingAgentServer, collection3Replica2SearchPort, collection3Replica2DistributedSearchPort);
    }
}
