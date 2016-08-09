package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain.admin.gis;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

public class GisAdminJarTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
    public void runAdminJarGisChangeset (){
        testExecutor.runAdminJar(sshClient, coordinatorServerIp, sphinx.console_USER_NAME, adminConfigLocation, adminJarFilePath, adminChangesetsPath + "gis/gis.xml");
    }
}
