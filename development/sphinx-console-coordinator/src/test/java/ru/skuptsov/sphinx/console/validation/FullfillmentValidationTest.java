package ru.skuptsov.sphinx.console.validation;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lnovikova on 3/11/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:sphinx.console-validation-fullfillment-context-test.xml"})
public abstract class FullfillmentValidationTest {

    protected final static Logger logger = LoggerFactory.getLogger(FullfillmentValidationTest.class);

    private static ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
    private static Validator validator = vf.getValidator();


    public static Server validServer = new Server();
    public static AdminProcess validAdminProcess = new AdminProcess();
    public static SearchConfigurationPortWrapper validPortWrapper = new SearchConfigurationPortWrapper();
    public static FieldMapping validFieldMapping = new FieldMapping();
    public static DataSource validDataSource = new DataSource();
    public static ConfigurationFields validConfigurationField = new ConfigurationFields();
    public static Configuration validConfiguration = new Configuration();
    public static ExternalAction validExternalAction = new ExternalAction();
    public static DeleteScheme validDeleteScheme = new DeleteScheme();
    public static Delta validDelta = new Delta();
    public static Replica validReplica = new Replica();
    public static SphinxProcess validSphinxProcess = new SphinxProcess();
    public static Collection validCollection = new Collection();
    public static CronScheduleWrapper validCronScheduleWrapper = new CronScheduleWrapper();
    public static ConfigurationTemplate validConfigurationTemplate = new ConfigurationTemplate();
    static {
        validServer.setIp("1.2.3.5");
        validServer.setName("server_name");

        validAdminProcess.setPort(1234);
        validAdminProcess.setType(ProcessType.COORDINATOR);
        validAdminProcess.setServer(validServer);

        validPortWrapper.setSearchConfigurationPort(1234);

        validFieldMapping.setSourceField("source_field");
        validFieldMapping.setSourceFieldType("source_field_type");
        validFieldMapping.setIndexField("index_field");
        validFieldMapping.setIndexFieldType(IndexFieldType.SQL_FIELD);
        validFieldMapping.setIsId(false);
        validFieldMapping.setConfiguration(validConfiguration);

        validDataSource.setType(DataSourceType.PGSQL);
        validDataSource.setHost("localhost");
        validDataSource.setPort(1234);
        validDataSource.setUser("user");
        validDataSource.setPassword("password");
        validDataSource.setSqlDb("db_name");

        validConfigurationTemplate.setName("template name");
        validConfigurationTemplate.setDefaultTemplate(false);
        validConfigurationTemplate.setSystemTemplate(false);
        validConfigurationTemplate.setType(ConfigurationType.CONFIGURATION);

        validConfigurationField.setFieldKey("field_key");
        validConfigurationField.setFieldValue("field_value");
        validConfigurationField.setConfiguration(validConfiguration);

        validConfiguration.setDatasource(validDataSource);

        LinkedHashSet<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();
        FieldMapping fieldMapping = validFieldMapping;
        fieldMappings.add(fieldMapping);
        validConfiguration.setFieldMappings(fieldMappings);

        Set<ConfigurationFields> sourceConfigurationFields = new HashSet<ConfigurationFields>();
        ConfigurationFields configurationField = validConfigurationField;
        sourceConfigurationFields.add(configurationField);
        validConfiguration.setSourceConfigurationFields(sourceConfigurationFields);

        Set<ConfigurationFields> searchConfigurationFields = new HashSet<ConfigurationFields>();
        configurationField = validConfigurationField;
        searchConfigurationFields.add(configurationField);
        validConfiguration.setSearchConfigurationFields(sourceConfigurationFields);

        validExternalAction.setType(ExternalActionType.SQL);
        validExternalAction.setCode("select * from servers");
        validExternalAction.setDataSource(validDataSource);

        validDeleteScheme.setType(DeleteSchemeType.BUSINESS_FIELD);
        validDeleteScheme.setFieldKey("field_key");
        validDeleteScheme.setFieldValueFrom("1");
        validDeleteScheme.setFieldValueTo("3");

        validDelta.setCollection(validCollection);
        validDelta.setType(DeltaType.DELTA);
        validDelta.setExternalAction(validExternalAction);
        validDelta.setDeleteScheme(validDeleteScheme);

        validReplica.setNumber(1l);
        validReplica.setCollection(validCollection);
        validReplica.setSearchProcess(validSphinxProcess);

        validSphinxProcess.setServer(validServer);
        validSphinxProcess.setCollection(validCollection);
        validSphinxProcess.setIndexName("index_name");
        validSphinxProcess.setConfiguration(validConfiguration);
        validSphinxProcess.setType(SphinxProcessType.INDEXING);
        validSphinxProcess.setReplica(validReplica);

        validCronScheduleWrapper.setCronSchedule("/50 * * * *");
        validCronScheduleWrapper.setEnabled(false);

        validCollection.setName("collection_name");
        validCollection.setType(CollectionType.MAIN_DELTA);
        validCollection.setDelta(validDelta);
        Set<Replica> replicas = new HashSet<Replica>();
        replicas.add(validReplica);
        validCollection.setReplicas(replicas);
    }

    public String getValidationError(Object object, Class<?>... groups) {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);

        if (constraintViolations.size() == 0)
            return null;

        StringBuilder validationError = new StringBuilder("Validation results of object " + object);

        for (ConstraintViolation<Object> cv : constraintViolations)
            validationError.append(MessageFormat.format("\nWarning! {0}={1} {2}",
                    cv.getPropertyPath(), cv.getInvalidValue(), cv.getMessage()));

        return validationError.toString();
    }

    public boolean hasValidationErrorForField(String errorMessage, String field) {
        return errorMessage != null && (errorMessage.contains(field+"=") || errorMessage.contains(field+".") || errorMessage.contains(field+"["));
    }

    public void checkInitErrors(Object targetObject){
        String validationErrors = getValidationError(targetObject);
        logger.info(validationErrors);
        Assert.assertNotNull(validationErrors);
    }

    public void checkValidationErrorFixed(Object targetObject, String fieldName, Object value){
        checkValidationError(targetObject, fieldName, value, true, true);
    }

    public void checkValidationErrorNotFixed(Object targetObject, String fieldName, Object value){
        checkValidationError(targetObject, fieldName, value, false, true);
    }

    public void checkValidationErrorForNotRequiredFixed(Object targetObject, String fieldName, Object value){
        checkValidationError(targetObject, fieldName, value, true, false);
    }

    public void checkValidationErrorForNotRequiredNotFixed(Object targetObject, String fieldName, Object value){
        checkValidationError(targetObject, fieldName, value, false, false);
    }

    public void checkValidationErrorNull(Object targetObject) {
        String validationErrors = getValidationError(targetObject);
        Assert.assertNull(validationErrors);
    }

    private void checkValidationError(Object targetObject, String fieldName, Object value, boolean fixed, boolean required) {
        try {
            String validationErrors = getValidationError(targetObject);
            if(required){
                Assert.assertTrue(hasValidationErrorForField(validationErrors, fieldName));
            }
            String methodName = "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length());
            Class valueClass = value.getClass();
            Method method = null;
            try{
                method = targetObject.getClass().getDeclaredMethod(methodName, valueClass);
            }
            catch (NoSuchMethodException e){
                //search for interfaces
                for(Class interfaceVar : valueClass.getInterfaces()){
                    try{
                        method = targetObject.getClass().getDeclaredMethod(methodName, interfaceVar);
                        if(method != null) break;
                    }
                    catch(NoSuchMethodException e2){

                    }
                }
            }
            if(method == null){
                logger.error("Didn't find " + methodName + " for value: " + value);
                Assert.assertTrue(false);
            }
            method.invoke(targetObject, value);
            validationErrors = getValidationError(targetObject);
            logger.info(validationErrors);
            Assert.assertEquals(hasValidationErrorForField(validationErrors, fieldName), !fixed);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.assertFalse(true);
        }
    }

}
