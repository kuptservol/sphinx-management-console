package ru.skuptsov.sphinx.console.test.integration.tests.query;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.util.ArrayList;
import java.util.List;

public class QueryTest extends TestEnvironmentPlainCollectionHelper {

    /**
     * Проверка запросов на прохождение теста на валидность
     */
    @Test
    public void checkQueries() {
        logger.info("--- START CHECK QUERIES ---");
        List<String> validQueries = new ArrayList<String>();
        validQueries.add("select * from test.files");
        validQueries.add("select * from test.files;");

        /**
         * CREATE OR REPLACE FUNCTION from_unixtime(integer) RETURNS timestamp AS 'SELECT $1::abstime::timestamp without time zone AS result' LANGUAGE 'SQL';
         *
         * по умолчанию такой функции нет в pgsql
         *
         * плюс, добавляем колонку, если нет:
         * ALTER TABLE public.files ADD COLUMN last_changed_date DATE NULL;
         */
        validQueries.add("select * from test.files where last_changed_date >= FROM_UNIXTIME($start) AND last_changed_date <= FROM_UNIXTIME($end);");

        testExecutor.checkQuery(validQueries, null, testDataSourceHost,
                testDataSourceType, testDataSourcePort, testDataSourceUsername, testDataSourcePassword, testDatasourceDB);
    }

}
