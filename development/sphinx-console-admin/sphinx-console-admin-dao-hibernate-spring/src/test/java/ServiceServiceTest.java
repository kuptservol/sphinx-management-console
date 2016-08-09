import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.admin.spring.service.api.CommandService;

/**
 * Created by Andrey on 12.08.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:sphinx.console-admin-hibernate-context-test.xml"})
public class ServiceServiceTest {

    @Autowired
    private CommandService concreteCommandService;

    @Before
    public void before() {
    }

    //@Ignore
    @Test
    public void first() {
        concreteCommandService.isExist("sd");
    }
}

