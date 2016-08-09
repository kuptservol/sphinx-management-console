package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed.admin.*;

/**
 * Created by Developer on 12.05.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AddDistributedCollection22AdminTest.class,
        ModifyDistributedCollectionAdminTest.class,
        CreateReplicaDistributedCollectionAdminTest.class,
        CreateSimpleCollectionReplicaInDistributedCollectionTest.class,
        ModifyDistributedCollectionReplicaPortAdminTest.class,
        NeedReloadAdminTest.class
})
public class CoordinatorIntegrationAdminDistributedCollectionTest {
}
