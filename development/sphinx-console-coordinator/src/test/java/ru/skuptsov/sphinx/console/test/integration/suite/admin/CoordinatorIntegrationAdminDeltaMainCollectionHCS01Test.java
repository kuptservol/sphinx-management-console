package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin.hcs01.*;

/**
 * Created by Developer on 12.05.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddDeltaMainCollectionHCS01AdminTest.class,
        RepairDeltaMainCollectionHCS01AdminTest.class,
        AddReplicaDeltaMainCollectionHCS01AdminTest.class,
        StopDeltaMainCollectionHCS01AdminTest.class,
        StartDeltaMainCollectionHCS01AdminTest.class,
        RemoveReplicaDeltaMainCollectionHCS01AdminTest.class

})
public class CoordinatorIntegrationAdminDeltaMainCollectionHCS01Test {
}
