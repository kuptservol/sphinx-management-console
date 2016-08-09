package ru.skuptsov.sphinx.console.test.integration.service.environment.helper;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;

/**
 * Created by Andrey on 29.01.2015.
 */
@RunWith(OrderedSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-plain-context.xml"})
public class TestEnvironmentPlainCollectionHelper extends TestEnvironmentHelper{

    public static final String TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME = "test_collection_simple_one_server";
    public static final String TEST_SNIPPET_COLLECTION_NAME = "test_snippet_collection";
    protected static CollectionWrapper PLAIN_COLLECTION_WRAPPER;

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

    @Value("${replica1.search.port}")
    public int replica1SearchPort;

    @Value("${replica1.distributed.search.port}")
    public int replica1DistributedSearchPort;

    @Value("${replica1.search.port.new}")
    public int replica1SearchPortNew;

    @Value("${snippet.search.port}")
    public int snippetSearchPort;

    @Value("${snippet.distributed.search.port}")
    public int snippetDistributedSearchPort;

    @Value("${snippet.search.port2}")
    public int snippetSearchPort2;

    @Value("${snippet.distributed.search.port2}")
    public int snippetDistributedSearchPort2;
}
