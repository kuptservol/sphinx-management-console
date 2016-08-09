package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.util.Date;

public class AddReplicaPlainSnippetCollectionTest extends TestEnvironmentPlainCollectionHelper {

    // ТЕСТ ПАДАЕТ! TODO ПРОВЕРИТЬ ДОБАВЛЕНИЕ РЕПЛИКИ


    @Test
//    @Ignore
    public void addReplicaTest() throws Throwable {
        logger.info("--- ADD REPLICA TEST ---");

        // добавляем текст в таблицу источник. После добавления реплики будем искать его в файлах сниппетов
        String searchText = textForSearchPrefix + " " + new Date().getTime() ;
        logger.info("Search text: " + searchText);

        testExecutor.addSearchDataIntoDBTable(testDataSourceHost,
                testDataSourcePort,
                testDatasourceDB,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                testDataSourceTableColumnSqlField,
                searchText);

        Integer id = (Integer)testExecutor.selectObjectFromDBTable(testDataSourceHost,
                testDataSourcePort,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                testDataSourceTableColumnId,
                testDataSourceTableColumnSqlField,
                "\'" + searchText + "\'");

        // добавляем реплику
        testExecutor.createReplica(TEST_SNIPPET_COLLECTION_NAME, searchAgentServer, snippetSearchPort2, snippetDistributedSearchPort2);
        logger.error("Wait for ");
        Thread.sleep(10000);

        // проверяем что снипет есть на всех репликах
        testExecutor.checkSnippetFile(TEST_SNIPPET_COLLECTION_NAME, id, searchText, sshClient, indexingAgentServerIp, searchingAgentServerIP);

    }

}
