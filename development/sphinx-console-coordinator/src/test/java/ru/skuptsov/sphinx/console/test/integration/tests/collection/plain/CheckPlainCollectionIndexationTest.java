package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Ignore;
import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class CheckPlainCollectionIndexationTest extends TestEnvironmentPlainCollectionHelper {

    /**
     * Сценарий "Проверка индексации_1"
     * Описание сценария - в индексируемой базе меняем значение - проводим индексацию коллекции - проверяем, что значение поменялось
     * Сценарий выполняется для коллекции TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME
     *      1.)в индексируемой базе вставляем новую запись
     *      2.) индексируем коллекцию - проверяем, что запись появилась
     *      3.)в индексируемой базе редактируем поля для записи выше
     *      4.) индексируем коллекцию - проверяем, что запись обновилась
     *
     * @throws Throwable
     */
    @Test
    @Ignore
    public void checkCollectionPlainIndexation() throws Throwable
    {

    }

}
