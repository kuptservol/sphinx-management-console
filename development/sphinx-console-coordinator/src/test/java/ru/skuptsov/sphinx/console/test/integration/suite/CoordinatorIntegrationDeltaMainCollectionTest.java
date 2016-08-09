package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.*;

@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddDeltaMainCollectionTest.class,
        RepairDeltaMainCollectionTest.class,
        DeleteDeltaMainRepairCollectionTest.class,
        AddReplicaDeltaMainCollectionTest.class,
        StopDeltaMainCollectionTest.class,
        StartDeltaMainCollectionTest.class,
        RebuildDeltaMainCollectionTest.class,
        MergeDeltaMainCollectionTest.class,
        ModifyDeltaMainCollectionAttributesNoChangeTest.class,
        FullIndexingDeltaMainCollectionAttributesNoChangeTest.class,
        SphinxQLDeltaMainTest.class,
        RepeatedTaskTest.class,
        QueryLogParseTest.class,
        RemoveReplicaDeltaMainCollectionTest.class,
        ScheduleDeltaMainCollectionDeltaTest.class,
        ScheduleDeltaMainCollectionMainTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-delta-main-context.xml"})
public class CoordinatorIntegrationDeltaMainCollectionTest {

}