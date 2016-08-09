package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.CheckPlainCollectionIndexationTest;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.admin.*;

/**
 * Created by Developer on 22.04.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddPlainCollectionAdminTest.class,
        CheckPlainCollectionIndexationTest.class,
        RepairPlainCollectionAdminTest.class,
        RebuildPlainCollectionAdminTest.class,
        ModifyPlainCollectionAttributesNoChangeAdminTest.class,
        FullIndexingPlainCollectionAttributesNoChangeAdminTest.class,
        FullIndexingPlainCollectionAttributesChangeAdminTest.class,
        ModifyPlainCollectionReplicaPortAdminTest.class,
        DeletePlainCollectionAdminTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-plain-context.xml"})
public class CoordinatorIntegrationAdminPlainCollectionTest {
}
