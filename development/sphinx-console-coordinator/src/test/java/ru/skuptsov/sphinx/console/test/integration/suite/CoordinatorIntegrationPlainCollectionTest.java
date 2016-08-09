package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.*;

@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddPlainCollectionTest.class,
        CheckPlainCollectionIndexationTest.class,
        RepairPlainCollectionTest.class,
        GetPlainCollectionWrapperTest.class,
        RebuildPlainCollectionTest.class,
        ModifyPlainCollectionAttributesNoChangeTest.class,
        FullIndexingPlainCollectionAttributesNoChangeTest.class,
        FullIndexingPlainCollectionAttributesChangeTest.class,
        SphinxQLPlainTest.class,
        ModifyPlainCollectionReplicaPortTest.class,
        SchedulePlainCollectionTest.class,
        DeletePlainCollectionTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-plain-context.xml"})
public class CoordinatorIntegrationPlainCollectionTest {

}