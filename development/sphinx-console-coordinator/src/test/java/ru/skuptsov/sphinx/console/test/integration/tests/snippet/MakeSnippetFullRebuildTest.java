package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.util.Date;

/**
 * Created by Developer on 07.07.2015.
 */
public class MakeSnippetFullRebuildTest extends TestEnvironmentPlainCollectionHelper {

    public static final String SNIPPET_FULL_REBUILD_SUFFIX = "full rebuild";

    @Test
    public void fullRebuildSnippetsConfiguration() throws Throwable {
        logger.info("--- FULL REBUILD SNIPPET CONFIGURATION ---");

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

        testExecutor.fullRebuildSnippets(TEST_SNIPPET_COLLECTION_NAME);

        testExecutor.checkSnippetFile(TEST_SNIPPET_COLLECTION_NAME, id, searchText + " " + SNIPPET_FULL_REBUILD_SUFFIX, sshClient, indexingAgentServerIp, searchingAgentServerIP);
    }
}