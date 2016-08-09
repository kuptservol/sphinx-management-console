package ru.skuptsov.sphinx.console.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.validation.fullfillment.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        AdminProcessTest.class,
        CollectionTest.class,
        CollectionWrapperTest.class,
        ConfigurationFieldsTest.class,
        ConfigurationTemplateTest.class,
        ConfigurationTest.class,
        CronSheduleWrapperTest.class,
        DataSourceTest.class,
        DeleteSchemeTest.class,
        DeltaTest.class,
        ExternalActionTest.class,
        FieldMappingTest.class,
        MoveProcessToServerWrapperTest.class,
        ReplicaTest.class,
        ReplicaWrapperTest.class,
        SearchConfigurationPortWrapperTest.class,
        ServerTest.class,
        ServerWrapperTest.class,
        SphinxProcessTest.class,
        TaskWrapperTest.class,
        UpdateScheduleWrapperTest.class

})
public class ValidationFullfillmentTests {

}