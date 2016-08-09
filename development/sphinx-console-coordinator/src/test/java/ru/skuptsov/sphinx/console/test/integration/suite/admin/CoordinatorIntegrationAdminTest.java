package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.prepare.PrepareTest;


/**
 * Created by Developer on 24.03.2015.
 */

/**
 * Класс для покрытия сервисов sphinx.console интеграционными тестами
 * Сценарии выполняются последовательно
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        PrepareTest.class,

        CoordinatorIntegrationAdminPlainCollectionTest.class,

        CoordinatorIntegrationAdminDeltaMainCollectionTest.class,

/*        тесты закомментированы, т.к. работают некорректно, Json неправильный.
        Например опять создаются коллекции test_collection_delta_main_distributed_server вместо соответствующих коллекций ЖКХ.
        Надо фиксить
        CoordinatorIntegrationAdminDeltaMainCollectionHCS01Test.class,

        CoordinatorIntegrationAdminDeltaMainCollectionHCS02Test.class,
*/
        CoordinatorIntegrationAdminDistributedCollectionTest.class,

        CoordinatorIntegrationAdminSnippetTest.class

})
public class CoordinatorIntegrationAdminTest {
}
