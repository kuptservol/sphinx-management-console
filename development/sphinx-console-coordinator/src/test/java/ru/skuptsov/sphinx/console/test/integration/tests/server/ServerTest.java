package ru.skuptsov.sphinx.console.test.integration.tests.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.ServerStatus;
import ru.skuptsov.sphinx.console.test.integration.service.ServerService;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentHelper;

import java.util.HashSet;
import java.util.Set;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
public class ServerTest extends TestEnvironmentHelper {

    @Autowired
    ServerService serverService;

    @Test
    public void checkServersAvailableTest() {
        Set<Server> serverS = new HashSet<Server>();
        serverS.add(coordinatorServer);
        serverS.add(searchAgentServer);
        serverS.add(indexingAgentServer);
        serverService.checkServersStatus(serverS, ServerStatus.RUNNING);
    }

    @Test
    public void checkServerUnavailable(){
        Server unavailableServer = serverService.createServer("UnavailableServer", "127.7.7.7");
        Set<Server> serverS = new HashSet<Server>();
        serverS.add(unavailableServer);
        serverService.checkServersStatus(serverS, ServerStatus.STOPPED);
    }

    @Test
    public void deleteServer(){
        serverService.deleteServer("UnavailableServer");
    }
}
