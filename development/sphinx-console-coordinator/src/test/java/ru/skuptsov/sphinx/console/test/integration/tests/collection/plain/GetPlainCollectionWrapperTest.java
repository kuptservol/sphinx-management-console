package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class GetPlainCollectionWrapperTest extends TestEnvironmentPlainCollectionHelper {

    /**
     * Сценарий "Получить обертку коллекции CollectionWrapper"
     * Описание сценария - Получить обертку коллекции CollectionWrapper
     * Сценарий
     *  -
     *  Список проверок
     *  1) В результате запроса возвращается не нулевой CollectionWrapper
     */
    @Test
//    @Ignore
    public void getPlainCollectionWrapperTest() throws InterruptedException {
        logger.info("--- GET PLAIN COLLECTION WRAPPER ---");

        PLAIN_COLLECTION_WRAPPER = testExecutor.getCollectionWrapper(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
    }

}
