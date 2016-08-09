package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Test;
import org.springframework.jdbc.core.RowMapper;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StopDeltaMainCollectionTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Остановка коллекции"
     * Описание сценария - происходит остановка коллекции test_collection_delta_main_distributed_server
     * Сценарий
     *  - происходит остановка коллекции test_collection_delta_main_distributed_server
     *  Список проверок
     *  - Проверяется, что по данному порту для коллекции больше не висит прослушивающих процессов
     *  - queryCollectionsInfo возвращает правильный статус
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void stopDeltaMainCollectionDistributedServer() throws InterruptedException {
        logger.info("--- STOP DELTA MAIN COLLECTION DISTRIBUTED SERVER ---");

        String collectionName = deltaMainCollectionName;

        List<Collection> collections = testExecutor.createJdbcTemplate("com.mysql.jdbc.Driver", jdbcUrl, jdbcUsername, jdbcPassword)

                .query("select * from sphinx.console.COLLECTION where collection_name = ?", new RowMapper<Collection>() {
                    @Override
                    public Collection mapRow(ResultSet resultSet, int i) throws SQLException {
                        Collection collection = new Collection();
                        collection.setName(resultSet.getString("collection_name"));
                        return collection;
                    }
                }, collectionName);

        logger.info("COLLECTION: " + collections);

        if (collections == null || collections.isEmpty()) {
            return;
        }

        testExecutor.stopAllProcesses(collectionName);

        logger.info("SEARCHING AGENT SERVER IP: " + searchingAgentServerIP + ", PASSWORD: " + searchingAgentServerRootPassword);

        testExecutor.getReplicasDataForStopProcesses(collectionName, searchingAgentServerIP, 22, "root", searchingAgentServerRootPassword);
    }
}
