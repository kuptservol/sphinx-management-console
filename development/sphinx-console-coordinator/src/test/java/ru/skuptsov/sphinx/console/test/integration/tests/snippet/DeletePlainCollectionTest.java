package ru.skuptsov.sphinx.console.test.integration.tests.snippet;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class DeletePlainCollectionTest extends TestEnvironmentPlainCollectionHelper {

    /**
     * Сценарий "Удаление коллекции"
     * Описание сценария - удаление коллекции TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME
     * Сценарий
     *  - Происходит удаление коллекции TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME
     *  Список проверок
     *  - Проверяется, что по данному порту для коллекции больше не висит прослушивающих процессов
     *  - Папка с данными по данной колллекции удалена успешно
     *  - Коллекция удалена из базы
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void deletePlainCollectionOneServer() throws InterruptedException {
        logger.info("--- START DELETING PLAIN COLLECTION ONE SERVER ---");
        testExecutor.deleteCollection(TEST_SNIPPET_COLLECTION_NAME, jdbcUrl, jdbcUsername, jdbcPassword, "root", searchingAgentServerRootPassword);
    }
}
