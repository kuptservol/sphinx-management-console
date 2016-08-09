package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class RepairPlainCollectionAdminTest extends TestEnvironmentPlainCollectionHelper {
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
        logger.info("--- REPAIR PLAIN COLLECTION ONE SERVER ---");

        testExecutor.repairCollection(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/plainTests/repairPlainCollectionOneServer/repairPlainCollectionOneServer.xml",
                                      TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME,
                                      jdbcUrl,
                                      jdbcUsername,
                                      jdbcPassword,
                                      searchAgentServer.getIp(),
                                      replica1SearchPort);
    }
}
