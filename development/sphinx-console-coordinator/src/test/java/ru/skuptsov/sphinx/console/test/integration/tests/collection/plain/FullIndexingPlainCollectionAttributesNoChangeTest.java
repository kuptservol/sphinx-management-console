package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.FullIndexingState;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

import java.util.Date;

public class FullIndexingPlainCollectionAttributesNoChangeTest extends TestEnvironmentPlainCollectionHelper {

    private static String fullRebuildSearchText;

    @Autowired
    protected RebuildPlainCollectionTest rebuildPlainCollectionTest;

    @Test
//    @Ignore
    public void plainCollectionfullIndexingStateNotRunningTest() {
        testExecutor.fullIndexingStateNotRunning(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
    }

    /* Проверка недоступности запуска копирования файлов фул-ребилда, если фул-ребилд еще не отрабатывал или его статус не равен READY_FOR_APPLY*/
    private void makeCollectionFullRebuildApplyFailureTest(String collectionName) throws Throwable {
        String serverName = null;
        // for single execution of test without executing all previous methods (in this case required variables will be uninitialized)
        if (indexingAgentServer == null) {
            serverName = coordinatorServerIp.equals(indexingAgentServerIp) ? COORDINATOR_SERVER_NAME : INDEXING_AGENT_SERVER_NAME;
        } else {
            serverName = indexingAgentServer.getName();
        }

        testExecutor.makeCollectionFullRebuildApplyFailure(collectionName, serverName);
    }

    @Test
//    @Ignore
    public void plainCollectionMakeCollectionFullRebuildApplyFailureTest() throws Throwable {
        makeCollectionFullRebuildApplyFailureTest(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
    }

    /**
     * Сценарий "Запустить полный пересбор"
     * Описание сценария - запуск полного пересбора коллекции (без изменения конфигурации и атрибутов)
     * Сценарий
     * 1) Добавление строки в таблицу-источник с текстом, по которому в качестве проверки будет проводиться поиск после Apply (makeCollectionFullRebuildAttributesNoChangeApplyTest)
     * 2) Запуск полного пересбора коллекции
     *  -
     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void makeCollectionFullRebuildAttributesNoChangeIndexTest() throws InterruptedException {
        logger.info("--- MODIFY COLLECTION FULL REBUILD ATTRIBUTES NO CHANGE INDEX ---");
        String serverName = null;
        // for single execution of test without executing all previous methods (in this case required variables will be uninitialized)
        if (indexingAgentServer == null) {
            serverName = coordinatorServerIp.equals(indexingAgentServerIp) ? COORDINATOR_SERVER_NAME : INDEXING_AGENT_SERVER_NAME;
        } else {
            serverName = indexingAgentServer.getName();
        }

        fullRebuildSearchText =  textForSearchPrefix + " " + new Date().getTime() ;
        testExecutor.addSearchDataIntoDBTable(testDataSourceHost, testDataSourcePort, testDatasourceDB, testDataSourceUsername, testDataSourcePassword,
                testDataSourceTable, testDataSourceTableColumnField, fullRebuildSearchText);

        testExecutor.makeCollectionFullRebuildIndex(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, serverName);
    }

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * После удачного завершения теста makeCollectionFullRebuildAttributesNoChangeIndexTest, статус должен быть READY_FOR_APPLY*/
    @Test
//    @Ignore
    public void fullIndexingStateReadyForApplyTest() {
        testExecutor.fullIndexingStateReadyForApply(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
    }

    /*После применения фул-ребилда, должна быть доступна операция очистки индексных данных фул-ребилда,
    после чего можно снова запускать фул-ребилд.
    После удаления проверяем доступность ребилда по коллекции и поиск*/
    @Test
//    @Ignore
    public void deleteFullIndexDataTest() throws Throwable {

        testExecutor.deleteFullIndexData(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME);
        rebuildPlainCollectionTest.rebuildPlainCollectionTest();

    }

    /*Проверка статуса после удаления индексных файлов фул-ребилда (статус должен быть NOT_RUNNING)*/
    @Test
//    @Ignore
    public void fullIndexingStateAfterDeleteFullIndexDataNotRunningTest() {
        plainCollectionfullIndexingStateNotRunningTest();
    }

    /*Повторный фул-ребилд*/
    @Test
//    @Ignore
    public void repeateMakeCollectionFullRebuildAttributesNoChangeIndexTest() throws InterruptedException {
        makeCollectionFullRebuildAttributesNoChangeIndexTest();
    }

    /**
     * Сценарий "Обновить коллекцию после полного пересбора без изменения атрибутов"
     * Описание сценария - обновление коллекцию после полного пересбора
     * Сценарий
     *  1) Проверить завершилась ли корректно задача полного пересбора коллекции
     *  2) Запустить обновление пересобранной коллекции на поисковых серварах

     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     * 2) Ко всем репликам коллекции порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     * 3) Поиск по тексту, добавленному в таблицу-источник в предыдущем тесте (makeCollectionFullRebuildAttributesNoChangeIndexTest)
     */
    @Test
//    @Ignore
    // TODO запускать метод только после удачного завершения полного пересбора коллеккции
    public void makeCollectionFullRebuildAttributesNoChangeApplyTest() throws Throwable {
        String serverName = null;
        // for single execution of test without executing all previous methods (in this case required variables will be uninitialized)
        if (indexingAgentServer == null) {
            serverName = coordinatorServerIp.equals(indexingAgentServerIp) ? COORDINATOR_SERVER_NAME : INDEXING_AGENT_SERVER_NAME;
        } else {
            serverName = indexingAgentServer.getName();
        }

        testExecutor.makeCollectionFullRebuildApply(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, serverName);
        testExecutor.checkSearchSuccess(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, indexingAgentServerIp, replica1SearchPort, testDataSourceTableColumnField, fullRebuildSearchText);
    }

    /*Проверка корректности получения статусов фул-ребилда FullIndexingState
    * После удачного завершения теста makeCollectionFullRebuildAttributesNoChangeApplyTest, статус должен быть OK*/
    @Test
//    @Ignore
    public void fullIndexingStateOkTest() {
        Assert.assertTrue(testExecutor.getCollectionInfo(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME).getFullIndexingResult().getFullIndexingState() == FullIndexingState.OK);
    }
}
