package ru.skuptsov.sphinx.console.test.integration.service.environment.helper;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionWrapper;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-distributed-context.xml"})
public class TestEnvironmentDistributedCollectionHelper extends TestEnvironmentHelper {
    protected static DistributedCollectionWrapper DISTRIBUTED_COLLECTION_WRAPPER;

    @Value("${distributed.collection.name.1}")
    public String distributedCollectionName1;

    @Value("${distributed.collection.name.2}")
    public String distributedCollectionName2;

    @Value("${collection.port}")
    public int collectionPort;

    @Value("${collection.port2}")
    public int collectionPort2;

    @Value("${new.distributed.replica.port}")
    public int newDistributedReplicaPort;

    @Value("${modified.distributed.replica.port}")
    public int modifiedDistributedReplicaPort;

    @Value("${distributed.simple.collection.name.1}")
    public String simpleCollectionName1;

    @Value("${distributed.simple.collection.name.2}")
    public String simpleCollectionName2;

    @Value("${distributed.simple.collection.name.3}")
    public String simpleCollectionName3;

    @Value("${admin.test.properties.path}")
    public String adminTestPropertiesPath;

    //-----------------------------------------------

    @Value("${jdbc.test.datasource.type}")
    public String testDataSourceType;

    @Value("${jdbc.test.datasource.host}")
    public String testDataSourceHost;

    @Value("${jdbc.test.datasource.username}")
    public String testDataSourceUsername;

    @Value("${jdbc.test.datasource.password}")
    public String testDataSourcePassword;

    @Value("${jdbc.test.datasource.table}")
    public String testDataSourceTable;

    @Value("${jdbc.test.datasource.table.column.id}")
    public String testDataSourceTableColumnId;

    @Value("${jdbc.test.datasource.table.column.field}")
    public String testDataSourceTableColumnField;

    @Value("${jdbc.test.datasource.table.column.field2}")
    public String testDataSourceTableColumnField2;

    @Value("${jdbc.test.datasource.table.column.sqlfield}")
    public String testDataSourceTableColumnSqlField;

    @Value("${jdbc.test.datasource.port}")
    public int testDataSourcePort;

    @Value("${jdbc.test.datasource.db}")
    public String testDatasourceDB;

    @Value("${jdbc.test.datasource.sql}")
    public String testDatasourceSql;

    //---------COLLECTION 1-------------

    @Value("${replica1.search.port}")
    public int replica1SearchPort;

    @Value("${replica1.distributed.search.port}")
    public int replica1DistributedSearchPort;

    @Value("${replica2.search.port}")
    public int replica2SearchPort;

    @Value("${replica2.distributed.search.port}")
    public int replica2DistributedSearchPort;

    @Value("${replica3.search.port}")
    public int replica3SearchPort;

    @Value("${collection2.replica3.search.port}")
    public int collection2Replica3SearchPort;

    @Value("${collection2.replica3.distributed.search.port}")
    public int collection2Replica3DistributedSearchPort;

    @Value("${collection2.replica3.distributed.search.port.new}")
    public int collection2Replica3DistributedSearchPortNew;

    @Value("${replica3.distributed.search.port}")
    public int replica3DistributedSearchPort;

    //---------COLLECTION 2-------------

    @Value("${collection2.replica1.search.port}")
    public int collection2Replica1SearchPort;

    @Value("${collection2.replica2.search.port}")
    public int collection2Replica2SearchPort;

    @Value("${collection2.replica1.distributed.search.port.new}")
    public int collection2Replica1DistributedSearchPortNew;

    @Value("${collection2.replica1.distributed.search.port}")
    public int collection2Replica1DistributedSearchPort;

    @Value("${collection2.replica2.distributed.search.port}")
    public int collection2Replica2DistributedSearchPort;

    //---------COLLECTION 3-------------

    @Value("${collection3.replica1.search.port}")
    public int collection3Replica1SearchPort;

    @Value("${collection3.replica2.search.port}")
    public int collection3Replica2SearchPort;

    @Value("${collection3.replica1.distributed.search.port.new}")
    public int collection3Replica1DistributedSearchPortNew;

    @Value("${collection3.replica1.distributed.search.port}")
    public int collection3Replica1DistributedSearchPort;

    @Value("${collection3.replica2.distributed.search.port}")
    public int collection3Replica2DistributedSearchPort;
}
