package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.admin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class AddPlainCollectionAdminTest extends TestEnvironmentPlainCollectionHelper {

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

        testExecutor.addCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/plainTests/addPlainCollection/addPlainCollection.xml",
                                   TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME,
                                   searchAgentServer.getIp(),
                                   replica1SearchPort,
                                   CollectionType.SIMPLE);
    }
}
