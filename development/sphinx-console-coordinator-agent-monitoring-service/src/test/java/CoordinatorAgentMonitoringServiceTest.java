import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.CoordinatorAgentMonitoringService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.mbean.CoordinatorAgentMonitoringServiceImpl;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessStatus;

/**
 * Created by Andrey on 08.08.2014.
 */

public class CoordinatorAgentMonitoringServiceTest {

    @Ignore
    @Test
    public void tTest() {
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService = new CoordinatorAgentMonitoringServiceImpl();
        Assert.assertEquals(ProcessStatus.SUCCESS, coordinatorAgentMonitoringService.isProcessAlive("fjhj"));
    }

    @Ignore
    @Test
    public void isAliveTest() {
        CoordinatorAgentMonitoringService coordinatorAgentMonitoringService = new CoordinatorAgentMonitoringServiceImpl();
        Assert.assertEquals(ProcessStatus.SUCCESS, coordinatorAgentMonitoringService.isProcessAlive("fjhj"));
    }

    @Ignore
    @Test
    public void configurationTest() {

    }
}
