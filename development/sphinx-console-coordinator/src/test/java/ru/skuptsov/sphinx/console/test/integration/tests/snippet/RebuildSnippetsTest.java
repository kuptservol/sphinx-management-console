package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.util.Date;

/**
 * Created by Developer on 06.07.2015.
 */
public class RebuildSnippetsTest extends TestEnvironmentPlainCollectionHelper {

    @Test
    public void rebuildSnippetsConfiguration() throws Throwable {
        logger.info("--- REBUILD SNIPPET CONFIGURATION ---");

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

        // check snippet_pre_query=false, snippet_post_query
        // after rebuild snippet they must be "true"
        Boolean preQuery = (Boolean)testExecutor.selectObjectFromDBTable(testDataSourceHost,
                testDataSourcePort,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                "snippet_pre_query",
                testDataSourceTableColumnId,
                id.toString());

        Boolean postQuery = (Boolean)testExecutor.selectObjectFromDBTable(testDataSourceHost,
                testDataSourcePort,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                "snippet_post_query",
                testDataSourceTableColumnId,
                id.toString());

        Assert.assertFalse(preQuery);
        Assert.assertFalse(postQuery);

        testExecutor.rebuildSnippets(TEST_SNIPPET_COLLECTION_NAME);

        preQuery = (Boolean)testExecutor.selectObjectFromDBTable(testDataSourceHost,
                testDataSourcePort,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                "snippet_pre_query",
                testDataSourceTableColumnId,
                id.toString());

        postQuery = (Boolean)testExecutor.selectObjectFromDBTable(testDataSourceHost,
                testDataSourcePort,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                "snippet_post_query",
                testDataSourceTableColumnId,
                id.toString());

        Assert.assertTrue(preQuery);
        Assert.assertTrue(postQuery);

        testExecutor.checkSnippetFile(TEST_SNIPPET_COLLECTION_NAME, id, searchText, sshClient, indexingAgentServerIp, searchingAgentServerIP);
    }
}