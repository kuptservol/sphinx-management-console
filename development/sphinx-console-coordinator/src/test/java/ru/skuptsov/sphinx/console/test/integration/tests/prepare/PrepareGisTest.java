package ru.skuptsov.sphinx.console.test.integration.tests.prepare;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentHelper;

import java.sql.SQLException;

/**
 * Created by Developer on 02.06.2015.
 */
@RunWith(OrderedSpringJUnit4ClassRunner.class)
public class PrepareGisTest extends TestEnvironmentHelper {

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
}
