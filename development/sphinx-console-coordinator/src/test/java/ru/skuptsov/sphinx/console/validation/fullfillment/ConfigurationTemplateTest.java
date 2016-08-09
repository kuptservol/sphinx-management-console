package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ConfigurationTemplateTest extends FullfillmentValidationTest {

    @Test
    public void validationTest(){
        ConfigurationTemplate targetObject = new ConfigurationTemplate();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "name", "");

        checkValidationErrorFixed(targetObject, "name", "template_name");

        checkValidationErrorFixed(targetObject, "defaultTemplate", false);

        checkValidationErrorFixed(targetObject, "systemTemplate", false);

        checkValidationErrorFixed(targetObject, "type", ConfigurationType.CONFIGURATION);

        checkValidationErrorNull(targetObject);
    }

}
