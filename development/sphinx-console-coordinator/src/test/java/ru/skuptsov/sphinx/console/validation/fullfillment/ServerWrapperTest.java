package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.ServerWrapper;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

import java.util.ArrayList;
import java.util.List;

public class ServerWrapperTest extends FullfillmentValidationTest {

    @Test
    public void validateObjectTest(){
        ServerWrapper targetObject = new ServerWrapper();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "server", new Server());

        checkValidationErrorFixed(targetObject, "server", validServer);

        List<AdminProcess> adminProcesses = new ArrayList<AdminProcess>();
        adminProcesses.add(new AdminProcess());

        checkValidationErrorForNotRequiredNotFixed(targetObject, "adminProcesses", adminProcesses);

        adminProcesses.clear();
        adminProcesses.add(validAdminProcess);

        checkValidationErrorForNotRequiredFixed(targetObject, "adminProcesses", adminProcesses);

        checkValidationErrorNull(targetObject);

    }

}
