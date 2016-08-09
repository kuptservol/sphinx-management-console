package ru.skuptsov.sphinx.console.test.integration.suite;

import net.jcip.annotations.NotThreadSafe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed.*;


/**
 * Created by Developer on 12.05.2015.
 */
@NotThreadSafe
@RunWith(Suite.class)
@Suite.SuiteClasses({

//        PrepareTest.class,
        DistributedPrepareTest.class,
        AddDistributedCollectionTest.class,
        AddDistributedCollection22Test.class,
        CreateReplicaSimpleCollectionDistributedCollectionTest.class,
        CreateReplicaDistributedCollectionTest.class,
        ModifyPortReplicaFromSimpleCollectionTest.class,
        DeleteReplicaFromSimpleCollectionTest.class,
        ReloadDistributedCollectionTest.class,
        SphinxQLDistributedTest.class,
        ModifyDistributedCollectionTest.class,
        ModifyPortReplicaDistributedCollectionTest.class,
        NeedReloadTest.class

})
public class CoordinatorIntegrationDistributedCollectionTest {
}
