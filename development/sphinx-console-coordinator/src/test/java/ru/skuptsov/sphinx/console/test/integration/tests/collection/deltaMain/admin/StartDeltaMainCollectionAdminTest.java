package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Test;
import org.springframework.jdbc.core.RowMapper;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StartDeltaMainCollectionAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Старт коллекции"
     * Описание сценария - происходит старт коллекции test_collection_delta_main_distributed_server
     * Сценарий
     *  - происходит старт коллекции test_collection_delta_main_distributed_server
     *  Список проверок
     *  - К коллекции по заданному порту получается сделать select с запросом размера коллекции через запрос
     *  select * from "+collectionName+" limit 0,0;show meta; - поле total_found
     *  - Размер коллекции совпадает с выдаваемым через queryCollectionsInfo
     *  - queryCollectionsInfo возвращает правильный статус
     *  - TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     */
    @Test
//    @Ignore
    public void startDeltaMainCollectionDistributedServer() throws InterruptedException {
        logger.info("--- START DELTA MAIN COLLECTION DISTRIBUTED SERVER ---");

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

        testExecutor.startAllProcesses(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/startAllProcesses/startAllProcesses.xml",
                                       collectionName);

        logger.info("SEARCHING AGENT SERVER IP: " + searchingAgentServerIP + ", PASSWORD: " + searchingAgentServerRootPassword);

        testExecutor.getReplicasDataForStartProcesses(collectionName, searchingAgentServerIP, 22, "root", searchingAgentServerRootPassword);
    }

}
