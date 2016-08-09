package ru.skuptsov.sphinx.console.test.integration.service.environment.helper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.test.integration.service.TestExecutor;

import java.sql.SQLException;

/**
 * Created by Andrey on 29.01.2015.
 */
@RunWith(OrderedSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-context.xml"})
public class TestEnvironmentHelper {

    protected final static Logger logger = LoggerFactory.getLogger(TestEnvironmentHelper.class);

    public static final String INDEXING_AGENT_SERVER_NAME = "IndexingAgentServer";
    public static final String COORDINATOR_SERVER_NAME = "CoordinatorServer";
    public static final String SEARCHING_AGENT_SERVER_NAME = "SearchingAgentServer";
    public static final String sphinx.console_CLEAR_SQL_RELATIVE_FILE_PATH = "/db/scripts/mysql/clear_data.sql";
    public static final String sphinx.console_START_DATA_RELATIVE_FILE_PATH = "/db/scripts/mysql/start_data.sql";
    public static final String sphinx.console_USER_NAME = "sphinx.console";

    public static final String PROJECT_PATH = System.getProperty("project.path") != null ? System.getProperty("project.path") : System.getProperty("user.dir");

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            logger.info("----------------------------STARTING TEST: " + description.getMethodName() + "--------------------------------");
        }
    };

    @Before
    public void before() throws SQLException, ClassNotFoundException{
        findServerAndProcess();
    }

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
    /**
     * Путь к проекту
     */

    @Value("${jdbc.db.url}")
    public String jdbcUrl;

    @Value("${jdbc.db.username}")
    public String jdbcUsername;

    @Value("${jdbc.db.password}")
    public String jdbcPassword;

    @Value("${text.for.search.prefix}")
    public String textForSearchPrefix;

    @Value("${ssh.client}")
    public String sshClient;

    protected static Server coordinatorServer;
    protected static Server searchAgentServer;
    protected static Server indexingAgentServer;

    protected static Boolean localDevMode = false;

    @Autowired
    protected TestExecutor testExecutor;

    public void findServerAndProcess() {
        if(searchAgentServer == null){
            searchAgentServer = testExecutor.setUpLocalServer(jdbcUrl, jdbcUsername, jdbcPassword, searchingAgentServerIP);
        }

        logger.info("SERVER AGENT SERVER: " + searchAgentServer);

        if(indexingAgentServer == null){
            indexingAgentServer = testExecutor.setUpLocalServer(jdbcUrl, jdbcUsername, jdbcPassword, indexingAgentServerIp);
        }
        logger.info("INDEX AGENT SERVER: " + indexingAgentServer);

        if(coordinatorServer == null){
            coordinatorServer = testExecutor.setUpLocalServer(jdbcUrl, jdbcUsername, jdbcPassword, coordinatorServerIp);
        }
        logger.info("COORDINATOR SERVER: " + coordinatorServer);
    }
}
