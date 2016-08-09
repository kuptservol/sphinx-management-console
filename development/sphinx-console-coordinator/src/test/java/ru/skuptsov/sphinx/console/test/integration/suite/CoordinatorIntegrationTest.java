package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.prepare.PrepareTest;
import ru.skuptsov.sphinx.console.test.integration.tests.query.QueryTest;
import ru.skuptsov.sphinx.console.test.integration.tests.server.ServerTest;
import ru.skuptsov.sphinx.console.test.integration.tests.validation.ValidationTest;
import ru.skuptsov.sphinx.console.validation.ValidationFullfillmentTests;

/**
 * Класс для покрытия сервисов sphinx.console интеграционными тестами
 * Сценарии выполняются последовательно
 * Created by SKuptsov on 03.01.15.
 */
@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

        PrepareTest.class,

        ServerTest.class,

        CoordinatorIntegrationPlainCollectionTest.class,

        CoordinatorIntegrationDeltaMainCollectionTest.class,

        CoordinatorIntegrationDeltaMainCollectionHCS01Test.class,

        CoordinatorIntegrationDeltaMainCollectionHCS02Test.class,

        CoordinatorIntegrationDistributedCollectionTest.class,

        CoordinatorIntegrationSnippetTest.class,

        // validation
        ValidationFullfillmentTests.class,
        ValidationTest.class,

        // queries
        QueryTest.class

})
public class CoordinatorIntegrationTest {

}