package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.hcs02.*;

@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddDeltaMainCollectionHCS02Test.class,
        RepairDeltaMainCollectionHCS02Test.class,
        DeleteDeltaMainRepairCollectionHCS02Test.class,
        AddReplicaDeltaMainCollectionHCS02Test.class,
        StopDeltaMainCollectionHCS02Test.class,
        StartDeltaMainCollectionHCS02Test.class,
        ScheduleDeltaMainCollectionHCS02DeltaTest.class,
        ScheduleDeltaMainCollectionHCS02MainTest.class,
        RemoveReplicaDeltaMainCollectionHCS02Test.class
        })
public class CoordinatorIntegrationDeltaMainCollectionHCS02Test {

}