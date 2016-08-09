package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

public class ReloadDistributedCollectionTest extends TestEnvironmentDistributedCollectionHelper {

    /**
     * Сценарий "Обновление конфигов для распределенной коллекции"
     * Описание сценария -
     * Сценарий
     *  - добавляется новая реплика
     * Список проверок :
     *  - Конфиг успешно обновленн
     *  - Флаг need_reload сброшен
     */
    @Test
    //@Ignore
    public void reloadDistributedCollectionTest() throws Throwable {
        testExecutor.reloadDistributedCollection(distributedCollectionName2, Boolean.FALSE, jdbcUrl, jdbcUsername, jdbcPassword);
    }
}