package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.SphinxQLDeltaMainTest;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        AddDeltaMainCollectionAdminTest.class,
        RepairDeltaMainCollectionAdminTest.class,
        AddReplicaDeltaMainCollectionAdminTest.class,
        StopDeltaMainCollectionAdminTest.class,
        StartDeltaMainCollectionAdminTest.class,
        RebuildDeltaMainCollectionAdminTest.class,
        MergeDeltaMainCollectionAdminTest.class,
        ModifyDeltaMainCollectionAttributesNoChangeAdminTest.class,
        FullIndexingDeltaMainCollectionAttributesNoChangeAdminTest.class,
        SphinxQLDeltaMainTest.class,
        RemoveReplicaDeltaMainCollectionAdminTest.class,
        DeleteDeltaMainCollectionAdminTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-delta-main-context.xml"})
public class CoordinatorIntegrationAdminDeltaMainCollectionTest {

}