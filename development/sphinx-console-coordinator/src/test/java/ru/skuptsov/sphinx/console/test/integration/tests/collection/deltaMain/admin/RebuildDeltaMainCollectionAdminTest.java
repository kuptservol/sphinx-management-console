package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.util.Date;

public class RebuildDeltaMainCollectionAdminTest extends TestEnvironmentDeltaMainCollectionHelper {

    /**
     * Сценарий "Пересбор дельта-мейн коллекции (delta)"
     *  -
     *  1) Добавление уникальной строки в таблицу-источник
     *  2) Пересбор (ребилд) коллекции
     *  3) Проверка на наличие результатов поиска по добавленным данным на всех нодах
     *  Список проверок
     * 1) TASK_LOG дошёл до последней стадии COMPLETED и завершился со статусом SUCCESS
     * 2) Поиск по добавленным данным на всех нодах дает результат
     *
     */
    @Test
//    @Ignore
    public void rebuildDeltaMainCollectionTest() throws Throwable {
        String idField = ((FieldMapping)fieldMappings.toArray()[0]).getSourceField();
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

        testExecutor.rebuildCollectionAdmin(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/deltaMainTests/rebuildCollection/rebuildCollection.xml");
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica1SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, searchingAgentServerIP, replica2SearchPort, textField, searchText);
        testExecutor.checkSearchSuccess(deltaMainCollectionName, indexingAgentServerIp, replica3SearchPort, textField, searchText);
    }
}
