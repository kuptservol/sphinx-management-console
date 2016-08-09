package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionType;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.test.integration.service.ScheduleService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class AddDeltaMainCollectionTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Autowired
    ScheduleService scheduleService;

    /**
     * Сценарий "Создание коллекции 2"
     * Описание сценария - создание коллекции типа delta+main в конфигурации на разных серверах
     * Сценарий
     *  - Создаётся коллекция с именем test_collection_delta_main_distributed_server типа delta + main search и index-агент на разных серверах
     *  - Запросы к main и delta указываются через custom-sql delta:
     *  delta : select id, value, is_deleted from delta
     *  main : select id, value, is_deleted from main WHERE id>=$start AND id<=$end(при этом в дополнительный праметры к датасорсу добавляются следующие :
     *  sql_query_range = SELECT MIN(id),MAX(id) FROM main ; sql_range_step = 1000 )
     *  - Добавляется поддержка удалённых записей
     *  (бизнес-поле, is_deleted, 1)
     *  - Добавляется external_action
     *  (sql, select * from main)
     * Список проверок :
     *  - Лог по коллекции успешно завершается этапом COMPLETED и status=SUCCESS
     *  - К коллекции по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     *  - Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     *  - Проверяется, что для записи с id=2 из распределённого индекса возвращается значение "Текст2"
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void addDeltaMainCollectionDistributedServer() throws Throwable {
        logger.info("--- ADD DELTA MAIN COLLECTION DISTRIBUTED SERVER ---");

        CollectionWrapper collectionWrapper = testExecutor.buildDeltaMainCollectionWrapper(cronMain, cronDelta, searchAgentServer, indexingAgentServer, deltaMainCollectionName,
                deltaMainDataSourcePort, deltaMainDataSourceHost, deltaMainDatasourceDB, deltaMainDataSourceUsername,
                deltaMainDataSourcePassword, deltaMainDataSourceType, deltaMainMainSql, deltaMainDeltaSql,
                deltaMainExternalActionCode, deltaMainMainSqlQueryRange, deltaMainMainSqlRangeStep, replica1SearchPort, deltaMainDeltaSqlQueryPre, deltaMainDeltaSqlQueryRange,
                deltaMainDeltaSqlQueryPostIndex, deltaMainDeltaSqlQueryRangeStep, deleteSchemeRequest, fieldMappings, replica1DistributedSearchPort);//TODO one used already

        testExecutor.addCollection(collectionWrapper, CollectionType.MAIN_DELTA, new Object[]{deltaMainIdToFindValue, deltaMainExpectedValue, ((FieldMapping)fieldMappings.toArray()[1]).getSourceField()});
        scheduleService.disableScheduleWithoutCheckFiringTasks(deltaMainCollectionName, ScheduledTaskType.INDEXING_DELTA);
        scheduleService.disableScheduleWithoutCheckFiringTasks(deltaMainCollectionName, ScheduledTaskType.MERGE_DELTA);

    }

}
