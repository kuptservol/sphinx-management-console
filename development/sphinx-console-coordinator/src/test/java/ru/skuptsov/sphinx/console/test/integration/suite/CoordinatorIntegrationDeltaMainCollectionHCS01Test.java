package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.hcs01.*;

@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddDeltaMainCollectionHCS01Test.class,
        RepairDeltaMainCollectionHCS01Test.class,
        DeleteDeltaMainRepairCollectionHCS01Test.class,
        AddReplicaDeltaMainCollectionHCS01Test.class,
        StopDeltaMainCollectionHCS01Test.class,
        StartDeltaMainCollectionHCS01Test.class,
        SphinxQLHCS01Test.class,
        ScheduleDeltaMainCollectionHCS01DeltaTest.class,
        ScheduleDeltaMainCollectionHCS01MainTest.class,
        RemoveReplicaDeltaMainCollectionHCS01Test.class
        })
public class CoordinatorIntegrationDeltaMainCollectionHCS01Test {

}