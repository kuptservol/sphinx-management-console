package ru.skuptsov.sphinx.console.test.integration.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.DataSourceType;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;

/**
 * Created by Andrey on 22.01.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-context.xml"})
public class EnvironmentAvailabilityTest {

    @Autowired
    TestExecutor testExecutor;
    /**
     * Адрес rest-сервисов
     */
    @Value("${server.uri}")
    public String serverURI;
    /**
     * IP сервера координатора
     */
    @Value("${coordinator.server.ip}")
    public String coordinatorServerIp;
    /**
     * Порт процесса координатора
     */
    @Value("${coordinator.server.port}")
    public int coordinatorServerPort;
    /**
     * IP поискового агента
     */
    @Value("${searching.agent.server.ip}")
    public String searchingAgentServerIP;
    /**
     * Порт поискового агента
     */
    @Value("${searching.agent.server.port}")
    public int searchingAgentServerPort;
    /**
     * Пароль для root для поискового агента
     */
    @Value("${searching.agent.server.root.password}")
    public String searchingAgentServerRootPassword;
    /**
     * IP индексового агента
     */
    @Value("${indexing.agent.server.ip}")
    public String indexingAgentServerIp;
    /**
     * IP индексового агента
     */
    @Value("${indexing.agent.server.port}")
    public int indexingAgentServerPort;
    /**
     * Пароль для root для индексового агента
     */
    @Value("${indexing.agent.server.root.password}")
    public String indexingAgentServerRootPassword;


    @Value("${jdbc.db.url}")
    public String jdbcUrl;

    @Value("${jdbc.db.username}")
    public String jdbcUsername;

    @Value("${jdbc.db.password}")
    public String jdbcPassword;

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

    @Value("${jdbc.test.datasource.port}")
    public int testDataSourcePort;

    @Value("${jdbc.test.datasource.db}")
    public String testDatasourceDB;

    @Value("${jdbc.test.datasource.sql}")
    public String testDatasourceSql;

    @Value("${replica2.search.port}")
    public int replica2SearchPort;

    @Value("${replica3.search.port}")
    public int replica3SearchPort;

    @Value("${delta.main.deltasql.query}")
    public String deltaMainDeltaSql;

    @Value("${delta.main.mainsql.query}")
    public String deltaMainMainSql;

    @Value("${delta.main.datasource.port}")
    public int deltaMainDataSourcePort;

    @Value("${delta.main.datasource.type}")
    public String deltaMainDataSourceType;

    @Value("${delta.main.datasource.host}")
    public String deltaMainDataSourceHost;

    @Value("${delta.main.datasource.username}")
    public String deltaMainDataSourceUsername;

    @Value("${delta.main.datasource.password}")
    public String deltaMainDataSourcePassword;

    @Value("${delta.main.datasource.db}")
    public String deltaMainDatasourceDB;

    private static final int TIME_OUT = 3000;

    @Test @Ignore
    public void indexingAgentServerSsh() {
        testExecutor.runSshCommand(indexingAgentServerIp, 22, "root", indexingAgentServerRootPassword, "echo ");
    }

    @Test @Ignore
    public void searchingAgentServerSsh() {
        testExecutor.runSshCommand(searchingAgentServerIP, 22, "root", searchingAgentServerRootPassword, "echo ");
    }

    @Test @Ignore
    public void indexingAgentServerReplicaPorts() {
        Assert.assertTrue("Indexing server not available on IP: " + indexingAgentServerIp + " PORT: " + replica2SearchPort, portAvailable(indexingAgentServerIp, replica2SearchPort, TIME_OUT));
        Assert.assertTrue("Indexing server not available on IP: " + indexingAgentServerIp + " PORT: " + (replica2SearchPort + 1), portAvailable(indexingAgentServerIp, replica2SearchPort + 1, TIME_OUT));
    }

    @Test @Ignore
    public void searchingAgentServerReplicaPorts() {
        Assert.assertTrue("Indexing server not available on IP: " + searchingAgentServerIP + " PORT: " + replica3SearchPort, portAvailable(searchingAgentServerIP, replica3SearchPort, TIME_OUT));
        Assert.assertTrue("Indexing server not available on IP: " + searchingAgentServerIP + " PORT: " + (replica3SearchPort + 1), portAvailable(searchingAgentServerIP, replica3SearchPort + 1, TIME_OUT));
    }

    @Test @Ignore
    public void testJdbcUrl() {
        DataSourceType dataSourceType = DataSourceType.getByTitle(testDataSourceType);
        JdbcTemplate jdbcTemplate = testExecutor.createJdbcTemplate(dataSourceType.getDriverClass(), jdbcUrl, jdbcUsername, jdbcPassword);
    }

    @Test @Ignore
    public void testDataSource() {
        Assert.assertTrue("Indexing server not available on IP: " + testDataSourceHost + " PORT: " + testDataSourcePort, portAvailable(testDataSourceHost, testDataSourcePort, TIME_OUT));
        DataSourceType dataSourceType = DataSourceType.getByTitle(testDataSourceType);
        String url = MessageFormat.format(dataSourceType.getUrlTemplate(), testDataSourceHost, "" + testDataSourcePort, testDatasourceDB);
        JdbcTemplate jdbcTemplate = testExecutor.createJdbcTemplate(dataSourceType.getDriverClass(), url, testDataSourceUsername, testDataSourcePassword);
        jdbcTemplate.execute("select " + testDataSourceTableColumnId + ", " + testDataSourceTableColumnField + " from " + testDataSourceTable);
        jdbcTemplate.execute(testDatasourceSql);

    }

    @Test @Ignore
    public void testDeltaMainDataSource() {
        Assert.assertTrue("Indexing server not available on IP: " + deltaMainDataSourceHost + " PORT: " + deltaMainDataSourcePort, portAvailable(deltaMainDataSourceHost, deltaMainDataSourcePort, TIME_OUT));
        DataSourceType dataSourceType = DataSourceType.getByTitle(testDataSourceType);
        String url = MessageFormat.format(dataSourceType.getUrlTemplate(), testDataSourceHost, "" + testDataSourcePort, testDatasourceDB);
        JdbcTemplate jdbcTemplate = testExecutor.createJdbcTemplate(dataSourceType.getDriverClass(), url, testDataSourceUsername, testDataSourcePassword);
        jdbcTemplate.execute("select " + testDataSourceTableColumnId + ", " + testDataSourceTableColumnField + " from " + testDataSourceTable);
        jdbcTemplate.execute(testDatasourceSql);
    }

    private boolean portAvailable(String ip, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
