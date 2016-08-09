package ru.skuptsov.sphinx.console.test.integration.tests.collection.plain.admin;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.admin.model.CommandResult;
import ru.skuptsov.sphinx.console.test.integration.service.TestChecker;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

public class ModifyPlainCollectionReplicaPortAdminTest extends TestEnvironmentPlainCollectionHelper {
    @Autowired
    private TestChecker testChecker;

    @Test
    public void modifyReplicaPort () throws Throwable {
        logger.info("--- MODIFY REPLICA PORT ON PLAIN COLLECTION ONE SERVER ---");

        CommandResult lastCommandResult =
                testExecutor.executeChangeset(PROJECT_PATH + "/development/sphinx.console-admin/sphinx.console-admin-service/src/main/resources/changesets/plainTests/modifyPlainCollectionReplicaPort/modifyReplicaPort.xml");

        Assert.assertEquals("Status returned non zero code: " + String.valueOf(lastCommandResult.getCode()), (Integer)0, lastCommandResult.getCode());
        testChecker.checkStatus(lastCommandResult.getTaskUid());
        testChecker.checkCollectionSize(TEST_COLLECTION_SIMPLE_ONE_SERVER_NAME, searchAgentServer.getIp(), replica1SearchPortNew);
    }
}
