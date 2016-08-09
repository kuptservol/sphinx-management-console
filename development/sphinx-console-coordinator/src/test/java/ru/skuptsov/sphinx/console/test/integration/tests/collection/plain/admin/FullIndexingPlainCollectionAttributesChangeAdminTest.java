package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.admin;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.FullIndexingState;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.sql.SQLException;
import java.util.Date;

public class FullIndexingPlainCollectionAttributesChangeAdminTest extends TestEnvironmentPlainCollectionHelper {

    private static String fullRebuildSearchText;

    /**
     * Сценарий "Редактирование коллекции c изменения атрибутов".
     * Сценарий
     * 1) Добавление строки с текстом в таблицу-источник для нового поля, которое будет добавлено в коллекции в процессе изменения атрибутов
     * 1) Получение CollectionWrapper простой коллекции TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME
     * 2) Добавление поля testDataSourceTableColumnField2 в коллекцию
     * 3) Выполнение изменения коллекции (кейс  - есть изменения атрибутов, т.е. нужен полный пересбор коллекции)
     *  -
     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     *
     *  -
     */
    @Test
//    @Ignore
    public void makeCollectionFullRebuildAttributesChangeIndexTest() throws InterruptedException, SQLException, ClassNotFoundException {
        fullRebuildSearchText =  textForSearchPrefix + " " + new Date().getTime() ;
        testExecutor.addSearchDataIntoDBTable(testDataSourceHost, testDataSourcePort, testDatasourceDB, testDataSourceUsername, testDataSourcePassword,
                testDataSourceTable, testDataSourceTableColumnField2, fullRebuildSearchText);

        testExecutor.modifyCollectionAttributes(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/plainTests/fullIndexingPlainCollectionAttributesChange/modifyCollectionAttributes.xml");
    }

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * После удачного завершения теста makeCollectionFullRebuildAttributesNoChangeIndexTest, статус должен быть READY_FOR_APPLY*/
    @Test
//    @Ignore
    public void fullIndexingStateAttrChangesReadyForUpdateTest() {
        testExecutor.fullIndexingStateReadyForApply(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
    }

    /**
     * Сценарий "Обновить коллекцию после полного пересбора"
     * Описание сценария - обновление коллекцию после полного пересбора
     * Сценарий
     *  1) Проверить завершилась ли корректно задача полного пересбора коллекции
     *  2) Запустить обновление пересобранной коллекции на поисковых серварах

     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     * 2) К реплике по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     * 3) Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     * 4) Поиск по тексту, добавленному в таблицу-источник в предыдущем тесте (modifyPlainCollectionAttributesAttributesChangeTest)
     */
    @Test
//    @Ignore
    public void makeCollectionFullRebuildAttributesChangeApplyTest() throws Throwable {
        testExecutor.executeChangesetWithCheck(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/plainTests/fullIndexingPlainCollectionAttributesChange/makeCollectionFullRebuildApply.xml");

        testExecutor.checkSearchSuccess(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, searchingAgentServerIP, replica1SearchPort, testDataSourceTableColumnField2, fullRebuildSearchText);
    }

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * После удачного завершения теста makeCollectionFullRebuildAttributesNoChangeApplyTest, статус должен быть OK*/
    @Test
//    @Ignore
    public void fullIndexingStateAttrChangesOkTest() {
        Assert.assertTrue(testExecutor.getCollectionInfo(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME).getFullIndexingResult().getFullIndexingState() == FullIndexingState.OK);
    }
}
