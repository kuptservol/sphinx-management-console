import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.AddCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.CreateReplicaTask;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import java.io.Serializable;

/**
 * Created by Andrey on 11.08.2014.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:sphinx.console-coordinator-service-context-test.xml"})
public class JmxTest implements Serializable {

    //@Autowired
    //@Qualifier("coordinatorAgentServiceClient")
    private CoordinatorAgentService coordinatorAgentSearchServiceClient;

    @Before
    @Ignore
    public void setUp() {
        coordinatorAgentSearchServiceClient = ApplicationContextProvider.getBean("coordinatorAgentServiceClient");
    }

    @Ignore
    @Test
    public void pingTest() {
        String result = coordinatorAgentSearchServiceClient.test();
        Assert.assertEquals("success", result);
    }

    @Ignore
    @Test
    public void setCoordinatorIndexTest() {
        Task task = new AddCollectionTask();
        String callbackHost ="192.168.0.21";
        String callbackPort ="8088";
        task.setCoordinatorAddress("service:jmx:rmi://" + callbackHost + "/jndi/rmi://" + callbackHost + ":" + callbackPort + "/coordinatorCallbackConnector");
        Status status = coordinatorAgentSearchServiceClient.setCoordinator(task);
        Assert.assertEquals(0, status.getCode());
    }

    @Ignore
    @Test
    public void startPushingFilesCommandTest() {
        CreateReplicaTask task = new CreateReplicaTask();
        task.setCollectionName("collection1");
        task.setReplicaNumber(3L);
        //String callbackHost ="192.168.0.21";
        //String callbackPort ="8088";
        //task.setCoordinatorAddress("service:jmx:rmi://" + callbackHost + "/jndi/rmi://" + callbackHost + ":" + callbackPort + "/coordinatorCallbackConnector");
        Status status = coordinatorAgentSearchServiceClient.startPushingFiles(task, task.getProcessName());
        Assert.assertEquals(0, status.getCode());
    }
}
