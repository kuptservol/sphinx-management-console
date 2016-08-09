package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.coordinator.model.DataSource;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class ConfigurationTest extends FullfillmentValidationTest {

    @Test
    public void configurationTest(){
        Configuration configuration = new Configuration();
        String validationErrors;
        validationErrors = getValidationError(configuration);
        logger.info(validationErrors);

        checkValidationErrorForNotRequiredNotFixed(configuration, "datasource", new DataSource());

        checkValidationErrorForNotRequiredFixed(configuration, "datasource", validDataSource);

        LinkedHashSet<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();
        fieldMappings.add(new FieldMapping());
        checkValidationErrorForNotRequiredNotFixed(configuration, "fieldMappings", fieldMappings);

        fieldMappings.clear();
        fieldMappings.add(validFieldMapping);
        checkValidationErrorForNotRequiredFixed(configuration, "fieldMappings", fieldMappings);

        Set<ConfigurationFields> sourceConfigurationFields = new HashSet<ConfigurationFields>();
        ConfigurationFields configurationField = new ConfigurationFields();
        configurationField.setFieldValue("field_value");
        sourceConfigurationFields.add(configurationField);
        checkValidationErrorForNotRequiredNotFixed(configuration, "sourceConfigurationFields", sourceConfigurationFields);

        sourceConfigurationFields.clear();
        sourceConfigurationFields.add(validConfigurationField);
        checkValidationErrorForNotRequiredFixed(configuration, "sourceConfigurationFields", sourceConfigurationFields);

        checkValidationErrorNull(configuration);

    }

}
