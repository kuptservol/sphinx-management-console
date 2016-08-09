package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin.hcs02.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddDeltaMainCollectionHCS02AdminTest.class,
        RepairDeltaMainCollectionHCS02AdminTest.class,
        AddReplicaDeltaMainCollectionHCS02AdminTest.class,
        StopDeltaMainCollectionHCS02AdminTest.class,
        StartDeltaMainCollectionHCS02AdminTest.class,
        RemoveReplicaDeltaMainCollectionHCS02AdminTest.class

        })
public class CoordinatorIntegrationAdminDeltaMainCollectionHCS02Test {

}