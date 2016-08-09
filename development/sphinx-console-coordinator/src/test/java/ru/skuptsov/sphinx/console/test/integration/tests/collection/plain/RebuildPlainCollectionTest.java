package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.util.Date;

public class RebuildPlainCollectionTest extends TestEnvironmentPlainCollectionHelper {

    /**
     * Сценарий "Пересбор простой коллекции (delta)"
     *  -
     *  1) Добавление уникальной строки в таблицу-источник
     *  2) Проверка на наличие результатов поиска по добавленным данным
     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     * 2) Поиск по добавленным данным дает результат
     *
     */
    @Test
//    @Ignore
    public void rebuildPlainCollectionTest() throws Throwable {
        String searchText = textForSearchPrefix + " " + new Date().getTime() ;

        testExecutor.addSearchDataIntoDBTable(testDataSourceHost,
                testDataSourcePort,
                testDatasourceDB,
                testDataSourceUsername,
                testDataSourcePassword,
                testDataSourceTable,
                testDataSourceTableColumnField,
                searchText);

        testExecutor.rebuildCollection(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
        testExecutor.checkSearchSuccess(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME,
                                        indexingAgentServerIp,
                replica1SearchPort,
                                        testDataSourceTableColumnField,
                                        searchText);
    }
}
