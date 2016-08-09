package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class RepairPlainCollectionTest extends TestEnvironmentPlainCollectionHelper {

    @Autowired
    ScheduleService scheduleService;
    /**
     * Сценарий "Восстановление простой созданной коллекции"
     * Описание сценария -
     * Сценарий
     *  - устанавливается флаг некорректного создания коллекции
     *  - выполняется редактирование атрибута(в данном кейсе запускается выполнение восстановления коллекции MODIFY_COLLECTION_ATTRIBUTES_RESTORE_FAILURE_CHAIN)
     * Список проверок :
     *  - Лог по коллекции успешно завершается этапом COMPLETED и status=SUCCESS
     *  - К коллекции по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     *  - Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     */
    @Test
//    @Ignore
    public void repairPlainCollectionOneServerTest() throws Throwable {
        CollectionWrapper collectionWrapper = PLAIN_COLLECTION_WRAPPER != null ? PLAIN_COLLECTION_WRAPPER : testExecutor.getCollectionWrapper(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);

        testExecutor.repairCollection(collectionWrapper, jdbcUrl, jdbcUsername, jdbcPassword);
        scheduleService.disableScheduleWithoutCheckFiringTasks(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, ScheduledTaskType.INDEXING_DELTA);
    }

}
