package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class AddPlainCollectionTest extends TestEnvironmentPlainCollectionHelper {

    @Autowired
    ScheduleService scheduleService;

    /**
     * Сценарий "Создание коллекции 1"
     * Описание сценария - создание коллекции типа plain в конфигурации на одном сервере
     * Сценарий
     *  - Создаётся коллекция с именем TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME типа plain на одном сервере координатора
     *  - Запрос указывается через выбор таблицы
     * Список проверок :
     *  - Лог по коллекции успешно завершается этапом COMPLETED и status=SUCCESS
     *  - К коллекции по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     *  - Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void addPlainCollectionOneServer() throws Throwable {
        logger.info("--- CREATE PLAIN COLLECTION ONE SERVER ---");
        CollectionWrapper collectionWrapper = testExecutor.buildPlainCollectionWrapper("0 /10 * * * ?",
                indexingAgentServer, indexingAgentServer, TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME,
                testDataSourcePort, testDataSourceHost, testDatasourceDB, testDataSourceUsername,
                testDataSourcePassword, testDataSourceType, null, testDataSourceTable, testDataSourceTableColumnId,
                testDataSourceTableColumnField, replica1SearchPort, replica1DistributedSearchPort,
                testDataSourceTableColumnSqlField);

        testExecutor.addCollection(collectionWrapper, CollectionType.SIMPLE);
        scheduleService.disableScheduleWithoutCheckFiringTasks(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, ScheduledTaskType.INDEXING_DELTA);
    }

}