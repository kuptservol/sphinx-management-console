package ru.skuptsov.sphinx.console.test.integration.tests.collection.distributed;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDistributedCollectionHelper;

/**
 * Created by Developer on 26.06.2015.
 */
public class NeedReloadTest extends TestEnvironmentDistributedCollectionHelper {


    @Test
    public void reloadAfterCreateReplicaTest() throws Throwable {
        logger.info("--- NEED RELOAD TEST AFTER CREATE OF REPLICA FOR " + distributedCollectionName2 + " ---");
        DistributedCollectionWrapper distributedCollectionWrapper = testExecutor.getDistributedCollectionWrapper(distributedCollectionName2);

        // check collectionSize > 0
        Assert.assertTrue(getDistributedCollectionSize(distributedCollectionWrapper) > 0);

        // stop all simple collections replicas
        for(SimpleCollectionWrapper simpleCollectionWrapper : distributedCollectionWrapper.getNodes()){
            for(ReplicaWrapper replicaWrapper : testExecutor.getReplicaWrappers(simpleCollectionWrapper.getCollectionName())){
                testExecutor.stopProcess(replicaWrapper.getCollectionName(), replicaWrapper.getReplicaNumber(),
                        replicaWrapper.getServer().getIp(), replicaWrapper.getSearchPort());
            }
        }
        // check search process unavailable after all replicas of simple collections switched off
        checkSearchProcessUnavailable(distributedCollectionWrapper);

        // add simple collection replica
        testExecutor.createReplicaSimpleCollectionDistributedCollection(distributedCollectionName2, simpleCollectionName2, indexingAgentServer,
                collection2Replica3SearchPort, collection2Replica3DistributedSearchPort, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
        // reload distributed collection
        testExecutor.reloadDistributedCollection(distributedCollectionName2, Boolean.FALSE, jdbcUrl, jdbcUsername, jdbcPassword);
        // check collectionSize > 0
        Assert.assertTrue(getDistributedCollectionSize(distributedCollectionWrapper) > 0);

    }

    @Test
    public void reloadAfterModifyReplicaPortTest() throws Throwable {
        logger.info("--- NEED RELOAD TEST AFTER MODIFY REPLICA PORT FOR " + distributedCollectionName2 + " ---");
        DistributedCollectionWrapper distributedCollectionWrapper = testExecutor.getDistributedCollectionWrapper(distributedCollectionName2);

        //modify third replica
        ReplicaWrapper replicaWrapper = testExecutor.getReplicaWrappers(simpleCollectionName2).get(2);
        replicaWrapper.setDistributedPort(collection2Replica3DistributedSearchPortNew);
        testExecutor.modifyPortReplicaFromSimpleCollection(distributedCollectionName2, replicaWrapper, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
        checkSearchProcessUnavailable(distributedCollectionWrapper);
        testExecutor.reloadDistributedCollection(distributedCollectionName2, Boolean.FALSE, jdbcUrl, jdbcUsername, jdbcPassword);

        Assert.assertTrue(getDistributedCollectionSize(distributedCollectionWrapper) > 0);
    }

    @Test
    public void reloadAfterRemoveReplicaTest() throws Throwable {
        logger.info("--- NEED RELOAD TEST AFTER REMOVE REPLICA FOR " + distributedCollectionName2 + " ---");
        DistributedCollectionWrapper distributedCollectionWrapper = testExecutor.getDistributedCollectionWrapper(distributedCollectionName2);

        //remove replica
        testExecutor.removeReplica(simpleCollectionName2, 3l, searchingAgentServerIP, searchingAgentServerRootPassword);
        checkSearchProcessUnavailable(distributedCollectionWrapper);
        testExecutor.checkNeedReloadCollection(distributedCollectionName2, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
        testExecutor.startAllProcesses(simpleCollectionName2);
        testExecutor.startAllProcesses(simpleCollectionName1);
        testExecutor.reloadDistributedCollection(distributedCollectionName2, Boolean.FALSE, jdbcUrl, jdbcUsername, jdbcPassword);
        testExecutor.checkNeedReloadCollection(distributedCollectionName2, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.FALSE);
        Assert.assertTrue(getDistributedCollectionSize(distributedCollectionWrapper) > 0);
    }

    @Test
    public void reloadAfterDeleteSimpleCollectionTest() throws Throwable {
        logger.info("--- NEED RELOAD TEST AFTER DELETE SIMPLE COLLECTION FOR " + distributedCollectionName2 + " ---");
        DistributedCollectionWrapper distributedCollectionWrapper = testExecutor.getDistributedCollectionWrapper(distributedCollectionName2);

        //delete collection
        testExecutor.deleteCollection(simpleCollectionName1, jdbcUrl, jdbcUsername, jdbcPassword, "root", searchingAgentServerRootPassword);
        checkSearchProcessUnavailable(distributedCollectionWrapper);
        testExecutor.checkNeedReloadCollection(distributedCollectionName2, jdbcUrl, jdbcUsername, jdbcPassword, Boolean.TRUE);
    }

    public Long getDistributedCollectionSize(DistributedCollectionWrapper wrapper) throws Throwable {
        return testExecutor.getCollectionSize(wrapper.getCollection().getName(),
                wrapper.getSearchServer().getIp(), wrapper.getSearchConfigurationPort().getSearchConfigurationPort());
    }

    public void checkSearchProcessUnavailable(DistributedCollectionWrapper distributedCollectionWrapper){
        testExecutor.checkSearchProcessUnavailable(distributedCollectionWrapper.getSearchServer().getIp(), distributedCollectionWrapper.getSearchConfigurationPort().getSearchConfigurationPort());
    }
}