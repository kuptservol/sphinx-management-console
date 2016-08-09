package ru.skuptsov.sphinx.console.test.integration.tests.prepare;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.test.integration.service.ServerService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentHelper;

import java.sql.SQLException;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
public class PrepareTest extends TestEnvironmentHelper {

    @Autowired
    ServerService serverService;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String value = System.getProperty("localDevMode");
        localDevMode = Boolean.valueOf(value);

        logger.info("SET UP LOCAL DEV MODE : " + localDevMode);
    }

    /**
     * Подготовка к тестам - очистка БД с конфигурацией и
     * папок с данными о коллекции на агентах
     */
    @Test
    public void prepareForTest() throws SQLException, ClassNotFoundException {
        if (!localDevMode) {

            // необходимо удалить все простые коллекции через таски координатора, т.к. вместе с ними удалятся шедулеры
            testExecutor.deleteAllSimpleCollections();
            logger.info("project.path: " + System.getProperty("project.path"));
            logger.info("user.dir: " + System.getProperty("user.dir"));
            logger.info("PROJECT_PATH: " + PROJECT_PATH);
            String clearDataSqlFilePath = PROJECT_PATH + sphinx.console_CLEAR_SQL_RELATIVE_FILE_PATH;
            logger.info("clearDataSqlFilePath: " + clearDataSqlFilePath);
            String startDataSqlFilePath = PROJECT_PATH + sphinx.console_START_DATA_RELATIVE_FILE_PATH;
            logger.info("startDataSqlFilePath: " + startDataSqlFilePath);
            testExecutor.clearConfigurationDataBase(jdbcUrl, jdbcUsername, jdbcPassword, clearDataSqlFilePath, startDataSqlFilePath);
            testExecutor.killallSearchdProcesses(indexingAgentServerIp, 22, "root", indexingAgentServerRootPassword);
            if(indexingAgentServerIp != searchingAgentServerIP){
                testExecutor.killallSearchdProcesses(searchingAgentServerIP, 22, "root", searchingAgentServerRootPassword);
            }
            testExecutor.clearAgentFiles(indexingAgentServerIp, 22, "root", indexingAgentServerRootPassword);
            if(indexingAgentServerIp != searchingAgentServerIP){
                testExecutor.clearAgentFiles(searchingAgentServerIP, 22, "root", searchingAgentServerRootPassword);
            }
        }
    }

    /**
     * Проверка на то, что конфигурация системы готова к проведению
     * интеграционного тестирования
     */
    @Test
    public void checkIfSystemReadyForTest() {
        testExecutor.checkIfServerProcessAvailable(searchingAgentServerIP, 22, "root", searchingAgentServerRootPassword, searchingAgentServerPort);
        if(searchingAgentServerIP != indexingAgentServerIp){
            testExecutor.checkIfServerProcessAvailable(indexingAgentServerIp, 22, "root", indexingAgentServerRootPassword, indexingAgentServerPort);
        }
    }

    /**
     * Сценарий "Создание сервера и процессов"
     * Описание сценария - создание серверов и процессов
     */
    @Test
    public void createServerAndProcess() {
        logger.info("ABOUT TO CREATE SERVER AND PROCESSES, DEV MODE: " + localDevMode);


        if (!localDevMode) {
            coordinatorServer = serverService.createServer(COORDINATOR_SERVER_NAME, coordinatorServerIp);
            if (coordinatorServerIp.equals(searchingAgentServerIP))
                searchAgentServer = coordinatorServer;
            else
                searchAgentServer = serverService.createServer(SEARCHING_AGENT_SERVER_NAME, searchingAgentServerIP);

            if (coordinatorServerIp.equals(indexingAgentServerIp))
                indexingAgentServer = coordinatorServer;
            else if (indexingAgentServerIp.equals(searchingAgentServerIP))
                indexingAgentServer = searchAgentServer;
            else indexingAgentServer = serverService.createServer(INDEXING_AGENT_SERVER_NAME, indexingAgentServerIp);

            testExecutor.addAdminProcess(coordinatorServer, coordinatorServerPort, ProcessType.COORDINATOR);
            testExecutor.addAdminProcess(searchAgentServer, searchingAgentServerPort, ProcessType.SEARCH_AGENT);
            testExecutor.addAdminProcess(indexingAgentServer, indexingAgentServerPort, ProcessType.INDEX_AGENT);
            if(!searchingAgentServerIP.equals(indexingAgentServerIp)){
                testExecutor.addAdminProcess(indexingAgentServer, indexingAgentServerPort, ProcessType.SEARCH_AGENT);
            }
        }
    }

}
