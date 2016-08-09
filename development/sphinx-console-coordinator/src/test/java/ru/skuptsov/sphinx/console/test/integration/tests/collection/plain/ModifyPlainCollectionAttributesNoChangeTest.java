package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesNoChangeTask;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class ModifyPlainCollectionAttributesNoChangeTest extends TestEnvironmentPlainCollectionHelper {

    /**
     * Сценарий "Редактирование коллекции без изменения атрибутов"
     * Сценарий
     * 1) Получение CollectionWrapper простой коллекции TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME
     * 2) Выполнение изменения коллекции (кейс  - нет изменения атрибутов, т.е. без пересбора коллекции)
     *  -
     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     * 2) К реплике по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     * 3) Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     *
     */
    @Test
//    @Ignore
    public void modifyPlainCollectionAttributesNoChangeTest() throws Throwable {
        logger.info("--- MODIFY PLAIN COLLECTION ATTRIBUTES NO CHANGE ---");
        CollectionWrapper collectionWrapper = PLAIN_COLLECTION_WRAPPER != null ? PLAIN_COLLECTION_WRAPPER : testExecutor.getCollectionWrapper(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);

        testExecutor.modifyCollectionAttributesNoChange(collectionWrapper, ModifyCollectionAttributesNoChangeTask.TASK_NAME);

    }

}
