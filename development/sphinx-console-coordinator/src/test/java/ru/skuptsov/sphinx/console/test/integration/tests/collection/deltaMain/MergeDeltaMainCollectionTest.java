package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesNoChangeTask;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.text.MessageFormat;
import java.util.Date;

public class MergeDeltaMainCollectionTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Проверка обновления данных"
     * Описание сценария - проверка обновления дельты новой информацией и merge delta в мэйн
     * Сценарий выполняется для коллекции test_collection_delta_main_distributed_server
     *      1.)в индексируемой базе вставляем новую запись
     *      2.) индексируем коллекцию - проверки :
     *              - проверяем, что запись появилась при запросе к distributed-индексу
     *      3.)в индексируемой базе редактируем поля для записи выше
     *      4.) индексируем коллекцию - проверки :
     *             - проверяем, что запись обновилась при запросе к distributed-индексу
     *      5.) запускаем принудительно merge
     *             - проверяем, что запись выше обновилась при запросе к  main-части-индекса
     */
    @Test
    public void deltaMainCollectionDistributedServerDeltaUpdateAndMainMerge() throws Throwable {

        Long merge_timestamp = new Date().getTime()/1000;
        CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(deltaMainCollectionName);
        ExternalAction externalAction = collectionWrapper.getCollection().getDelta().getExternalAction();
        externalAction.setCode(MessageFormat.format("insert into test.merge_date (merge_timestamp) values(TO_TIMESTAMP({0}));", merge_timestamp.toString()));
        testExecutor.modifyCollectionAttributesNoChange(collectionWrapper, ModifyCollectionAttributesNoChangeTask.TASK_NAME);

        String textField = ((FieldMapping)fieldMappings.toArray()[1]).getSourceField();
        String searchText =  textForSearchPrefix + " " + new Date().getTime() ;

        testExecutor.addSearchDataIntoDBTable(deltaMainDataSourceHost,
                deltaMainDataSourcePort,
                deltaMainDatasourceDB,
                deltaMainDataSourceUsername,
                deltaMainDataSourcePassword,
                deltaMainTable,
                textField,
                searchText);

        testExecutor.rebuildCollection(deltaMainCollectionName);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, searchText);

        String newSearchText =  textForSearchPrefix + " " + new Date().getTime();

        testExecutor.updateSearchDataInDBTable(deltaMainDataSourceHost,
                deltaMainDataSourcePort,
                deltaMainDatasourceDB,
                deltaMainDataSourceUsername,
                deltaMainDataSourcePassword,
                deltaMainTable,
                textField,
                searchText,
                newSearchText);

        testExecutor.rebuildCollection(deltaMainCollectionName);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, newSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, newSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, newSearchText);

        testExecutor.mergeCollection(deltaMainCollectionName);
        testExecutor.checkSearchSuccess(deltaMainCollectionName + "_" + IndexType.MAIN.getTitle(), searchingAgentServerIP, replica1SearchPort, textField, newSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName + "_" + IndexType.MAIN.getTitle(), searchingAgentServerIP, replica2SearchPort, textField, newSearchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName + "_" + IndexType.MAIN.getTitle(), indexingAgentServerIp, replica3SearchPort, textField, newSearchText);

        testExecutor.checkMergeDate(deltaMainDataSourceHost, deltaMainDataSourcePort, deltaMainDatasourceDB, deltaMainDataSourceUsername, deltaMainDataSourcePassword, merge_timestamp);
    }

}
