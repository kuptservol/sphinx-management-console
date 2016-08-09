package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class RepairDeltaMainCollectionTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Восстановление первоначально некорректно созданной коллекции через редактирование"
     * Описание сценария - создание коллекции типа delta+main в конфигурации на разных серверах с дефектом и последующее редактирование
     * Сценарий
     *  - Создаётся коллекция с именем test_collection_delta_main_distributed_server_repair типа delta + main search и index-агент на разных серверах
     *  - Запрос указывается через выбор таблицы
     *  - В конфигурации указывается намеренно ошибка в конфигурации(напр. добавляется в конфиг параметр с ошибкой, напр.  mem_lim вместо mem_limit)
     *  - Получаем ошибку создания - редактируем коллекцию, исправляя ошибку
     * Список проверок :
     *  - Лог по коллекции успешно завершается этапом COMPLETED и status=SUCCESS
     *  - К коллекции по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     *  - Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void repairDeltaMainCollectionDistributedServerRepair() throws Throwable {
        logger.info("--- REPAIR DELTA MAIN COLLECTION DISTRIBUTED SERVER TEST ---");
        String failedDeltaMainSqlQueryRange = "select dummy, server_id, name, is_deleted from SERVER_QQQ";
        CollectionWrapper collectionWrapper = testExecutor.buildDeltaMainCollectionWrapper(cronMain, cronDelta, searchAgentServer, indexingAgentServer, deltaMainCollectionName+REPAIR_SUFFIX,
                deltaMainDataSourcePort, deltaMainDataSourceHost, deltaMainDatasourceDB, deltaMainDataSourceUsername,
                deltaMainDataSourcePassword, deltaMainDataSourceType, deltaMainMainSql, deltaMainDeltaSql, deltaMainExternalActionCode, failedDeltaMainSqlQueryRange, deltaMainMainSqlRangeStep, repairDeltaMainIndexPort,
                deltaMainDeltaSqlQueryPre, deltaMainDeltaSqlQueryRange, deltaMainDeltaSqlQueryPostIndex, deltaMainDeltaSqlQueryRangeStep, deleteSchemeRequest, fieldMappings, repairDeltaMainDistributedIndexPort);

        testExecutor.repairDeltaMainCollectionDistributedServerRepair(collectionWrapper, deltaMainMainSqlQueryRange);
    }
}
