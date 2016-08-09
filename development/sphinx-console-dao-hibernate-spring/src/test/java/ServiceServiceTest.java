import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.common.DateDetailing;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.*;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuerySearchParameters;
import ru.skuptsov.sphinx.console.spring.service.api.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrey on 12.08.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:sphinx.console-hibernate-context-test.xml"})
public class ServiceServiceTest {

    @Autowired
    private DeltaService deltaService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ReplicaService replicaService;

    @Autowired
    private AdminProcessService adminProcessService;

    @Autowired
    private ServerService serverService;

    @Autowired
	private ConfigurationService configurationService;

    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Autowired
	private ConfigurationTemplateService configurationTemplateService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private SearchQueryService searchQueryService;

    @Autowired
    private SearchQueryResultService searchQueryResultServiceService;

    @Before
    public void before() {
    }

    @Ignore
    @Test
    @Transactional
    public void getAdminProcessTest() {
        Assert.assertEquals(ProcessType.COORDINATOR, adminProcessService.getAdminProcess(ProcessType.COORDINATOR, null).getType());
        Assert.assertEquals(ProcessType.SEARCH_AGENT, adminProcessService.getAdminProcess(ProcessType.SEARCH_AGENT, null).getType());
        Assert.assertEquals(ProcessType.INDEX_AGENT, adminProcessService.getAdminProcess(ProcessType.INDEX_AGENT, null).getType());

        Assert.assertEquals(ProcessType.COORDINATOR, serverService.getAdminProcess(ProcessType.COORDINATOR, "").getType());
        Assert.assertEquals(ProcessType.SEARCH_AGENT, serverService.getAdminProcess(ProcessType.SEARCH_AGENT, "").getType());
        Assert.assertEquals(ProcessType.INDEX_AGENT, serverService.getAdminProcess(ProcessType.INDEX_AGENT, "").getType());
    }

    @Ignore
    @Test
    public void updateConfigurationTest() {
    	 Configuration configuration = configurationService.getConfiguration("index-conf-collection23");
    }

    @Ignore
    @Test
    public void updateConfigurationTemplateTest() {
    	ConfigurationTemplate template = configurationTemplateService.getConfigurationTemplate(8L);

        if (!template.getConfigurationFields().isEmpty()) {
            configurationService.clearConfigurationFields(8L);
            template.getConfigurationFields().clear();
        }


        Set<ConfigurationFields> configurationFields = new HashSet<ConfigurationFields>();
        ConfigurationFields configurationField = new ConfigurationFields();
        configurationField.setFieldKey("mem_limit");
        configurationField.setFieldValue("32M");
        //configurationField.setId(21L);


        ConfigurationFields configurationField2 = new ConfigurationFields();
        configurationField2.setFieldKey("mem_limit");
        configurationField2.setFieldValue("32M");


        configurationFields.add(configurationField);
        configurationFields.add(configurationField2);

        template.setConfigurationFields(configurationFields);

        configurationTemplateService.updateConfigurationTemplate(template);

    }

    @Ignore
    @Test
    public void clearSourceConfigurationFieldsTest() {
    	System.out.println("ABOUT TO DELETE SOURCE CONFIGURATION FIELDS...");
    	configurationService.clearSourceConfigurationFields(1L);
    }

    @Ignore
    @Test
    public void clearFieldMappingsTest() {
    	Configuration configuration = configurationService.getConfiguration("search-conf-collection19");
    	configurationService.clearFieldMappings(configuration.getId());
    }

    @Ignore
    @Test
    public void saveConfigurationTest() {
    	DataSource dataSource = new DataSource();
        dataSource.setPort(3306);
        dataSource.setHost("192.168.211.111");
        dataSource.setSqlDb("sphinx.console");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setType(DataSourceType.MYSQL);

        Configuration searchConfiguration = new Configuration();

        searchConfiguration.setName("CCC search 777");
        searchConfiguration.setDatasource(dataSource);

        //configurationTemplateService shouldn't save by cascade
        ConfigurationTemplate configurationTemplate = configurationTemplateService.getConfigurationTemplate(1L);
        ConfigurationTemplate searchConfigurationTemplate = configurationTemplateService.getConfigurationTemplate(2L);
        ConfigurationTemplate indexerConfigurationTemplate = configurationTemplateService.getConfigurationTemplate(3L);

        ConfigurationFields configurationField = new ConfigurationFields();
        configurationField.setFieldKey("sql_query");
        configurationField.setFieldValue("select * from ..... ");
        configurationField.setConfigurationType(ConfigurationType.SOURCE);
        configurationField.setConfiguration(searchConfiguration);
        searchConfiguration.getSourceConfigurationFields().add(configurationField);


        ConfigurationFields configurationField2 = new ConfigurationFields();
        configurationField2.setFieldKey("doc_info");
        configurationField2.setFieldValue("doc_info");
        configurationField2.setConfigurationType(ConfigurationType.SEARCH);
        configurationField2.setConfiguration(searchConfiguration);
        searchConfiguration.getSearchConfigurationFields().add(configurationField2);

        searchConfiguration.setConfigurationTemplate(configurationTemplate);
        searchConfiguration.setSearchConfigurationTemplate(searchConfigurationTemplate);
        searchConfiguration.setIndexerConfigurationTemplate(indexerConfigurationTemplate);

        configurationService.save(searchConfiguration);
    }

    @Ignore
    @Test
    public void deleteConfigurationTest() {
        Configuration configuration = configurationService.getConfiguration("CCC search 777");
        configurationService.delete(configuration);
    }

    @Ignore
    @Test
    public void saveConfigurationFieldsTest() {
        Configuration configuration = configurationService.findById(555L);

        ConfigurationFields configurationField = new ConfigurationFields();
        configurationField.setConfigurationType(ConfigurationType.SEARCH);
        configurationField.setFieldKey("doc_info");
        configurationField.setFieldKey("doc_info");
        configurationField.setFieldValue(".....");
        configurationField.setConfiguration(configuration);

        ConfigurationFields configurationField2 = new ConfigurationFields();
        configurationField2.setConfigurationType(ConfigurationType.SEARCH);
        configurationField2.setFieldKey("listen");
        configurationField2.setFieldValue("7777");
        configurationField2.setConfiguration(configuration);

        ConfigurationFields configurationField3 = new ConfigurationFields();
        configurationField3.setConfigurationType(ConfigurationType.SOURCE);
        configurationField3.setFieldKey("sql_query");
        configurationField3.setIndexType(IndexType.MAIN);
        configurationField3.setFieldValue("select bla-bla-bla ...");
        configurationField3.setConfiguration(configuration);

        ConfigurationFields configurationField4 = new ConfigurationFields();
        configurationField4.setConfigurationType(ConfigurationType.SOURCE);
        configurationField4.setFieldKey("sql_query");
        configurationField4.setIndexType(IndexType.DELTA);
        configurationField4.setFieldValue("select bla-bla-bla ...");
        configurationField4.setConfiguration(configuration);

        //configuration.setSearchConfigurationFields(searchConfigurationFields);
        configuration.getSearchConfigurationFields().add(configurationField);
        configuration.getSearchConfigurationFields().add(configurationField2);
        configuration.getSourceConfigurationFields().add(configurationField3);
        configuration.getSourceConfigurationFields().add(configurationField4);

        configurationService.save(configuration);
    }

    @Ignore
    @Test
    public void getSqlQueryServiceTest() {
        Configuration configuration = configurationService.findById(555L);
        System.out.println(configuration.getSearchListenPort());
        System.out.println(configuration.getMainSqlQuery());
        System.out.println(configuration.getDeltaSqlQuery());

    }

    @Ignore
    @Test
    public void saveSearchPortTest() {
        String searchPort = "9999";
        ConfigurationFields configurationField = configurationFieldsService.getSearchPort(555L);
        configurationField.setFieldValue(searchPort);
        configurationFieldsService.save(configurationField);
        Configuration configuration = configurationService.findById(555L);
        Assert.assertEquals(searchPort, configuration.getSearchListenPort());
    }

    @Ignore
    @Test
    public void processServiceTest() {
        SphinxProcess sphinxProcess = processService.findById(1L);
        sphinxProcess.getReplica();
    }

    @Ignore
    @Test
    public void deltaTest() {
        Delta delta = deltaService.findById(1L);
        delta.getCollection();
        delta.getExternalAction();
    }
    
    
    @Ignore
    @Test
    public void saveDeltaTest() {
        Delta delta = new Delta();
        String collectionName = "collection1";
        Collection collection = collectionService.getCollection(collectionName);
        delta.setCollection(collection);
        delta.setType(DeltaType.DELTA);

        DeleteScheme deleteScheme = new DeleteScheme();
        deleteScheme.setType(DeleteSchemeType.BUSINESS_FIELD);
        deleteScheme.setFieldKey("key");
        deleteScheme.setFieldValueFrom("1");
        deleteScheme.setFieldValueTo("3");
        delta.setDeleteScheme(deleteScheme);

        ExternalAction externalAction = new ExternalAction();
        
        delta.setExternalAction(externalAction);
        delta.setPeriod(new Date());
        
        externalAction.setCode("select * from servers");
        externalAction.setType(ExternalActionType.SQL);
        
        
        collection.setDelta(delta);
        collectionService.save(collection);
    }


    @Ignore
    @Test
    public void replicaTest() {
        //String collectionName = "collection_FFF";]
        String collectionName = "collection1";
        Collection collection = collectionService.getCollection(collectionName);
        Replica replica = new Replica();
        Long index = replicaService.createReplicaNumber(collectionName);
        replica.setNumber(index);
        replica.setCollection(collection);
        collection.getReplicas().add(replica);
        //collectionService.save(collection);
    }

    @Ignore
    @Test
    public void deleteReplicaTest() {
        String collectionName = "collection1";
        Replica replica = replicaService.findReplicaByNumber(collectionName, 3L);
        SphinxProcess sphinxProcess = processService.findByReplica(replica);
       // ConfigurationFields configurationFields = configurationFieldsService.getByConfigurationId(sphinxProcess.getConfiguration().getId());
       // configurationFieldsService.delete(configurationFields);
        //configurationService.deleteById(4L);
        processService.delete(sphinxProcess);
    }

    @Test
    @Ignore
    public void deleteReplicaTest2() {
        Replica replica = replicaService.findById(15L);
        SphinxProcess sphinxProcess = processService.findByReplica(replica);
        processService.delete(sphinxProcess);
    }

    @Test
    @Ignore
    public void deleteCollectionTest() {
        Collection collection = collectionService.findById(4L);
        collectionService.delete(collection);
    }

    @Ignore
    @Test
    public void findProcessTest() {
        String collectionName = "collection1";
        SphinxProcess sphinxProcess = processService.findSearchProcess(collectionName, 2L);
        System.out.println(sphinxProcess.getReplica().getNumber());
    }

    @Ignore
    @Test
    public void findFullIndexingLogTest() {
        String collectionName = "collection1";
        SphinxProcess sphinxProcess = processService.findFullIndexingProcess(collectionName);
        List<ActivityLog> logs = activityLogService.getSphinxProcessLogs(sphinxProcess);

        System.out.println();
    }

    @Ignore
    @Test
    public void saveExternalActionTest() {
        Collection collection = new Collection();
        collection.setName("icollection1");
        collection.setType(CollectionType.MAIN_DELTA);
        collection.setDescription("icollection1");

        Delta delta = new Delta();
        delta.setCollection(collection);
        delta.setType(DeltaType.DELTA);

        ExternalAction externalAction = new ExternalAction();
        externalAction.setCode("select * from servers");
        externalAction.setType(ExternalActionType.SQL);

        DataSource dataSource = new DataSource();
        dataSource.setName("dataSourceTest");
        dataSource.setPort(3306);
        dataSource.setHost("192.168.211.111");
        dataSource.setSqlDb("sphinx.console");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setType(DataSourceType.MYSQL);

        externalAction.setDataSource(dataSource);

        delta.setExternalAction(externalAction);
        delta.setPeriod(new Date());

        collection.setDelta(delta);

        collectionService.save(collection);
    }

    @Test
    public void logsTest() {
        ActivityLogSearchParameters parameters = new ActivityLogSearchParameters();
        parameters.setDateFrom(new Date());
        List<ActivityLog> logs = activityLogService.getActivityLog(parameters);

        System.out.println();
    }

    @Test
    public void searchQueryTest(){
        SearchQuerySearchParameters parameters = new SearchQuerySearchParameters();
        parameters.setStart(0);
        parameters.setLimit(10);
        parameters.setCollectionName("test_collection_simple_one_server");
/*        parameters.setReplicaName(new ReplicaName());
        parameters.getReplicaName().setServerName("CoordinatorServer");
        parameters.getReplicaName().setPort("9307");*/
//        parameters.setCollectionId(2);
//        parameters.setReplicaId(1);
//        parameters.setDateFrom(new Date(new Date().getTime() - 8000000000L));
//        parameters.setDateTo(new Date());
//        parameters.setTotalTimeMin(-1);
        parameters.setResultCountMin(20);
//        parameters.setOffsetNotZero(true);
//        parameters.setResultCountZero(true);
        List<SearchQueryGrouped> result = searchQueryService.getSearchQueries(parameters);
        Long count = searchQueryService.getForCount(parameters);
        System.out.println(result.size());
    }

    @Test
    public void searchQueryHistoryTest(){
        SearchQueryHistorySearchParameters parameters = new SearchQueryHistorySearchParameters();
        parameters.setCollectionName("test_collection_delta_main_distributed_server");
        parameters.setReplicaName(new ReplicaName());
        parameters.getReplicaName().setServerName("CoordinatorServer");
        parameters.getReplicaName().setPort(9307);
                parameters.setQuery(" SELECT * FROM test_collection_delta_main_distributed_server LIMIT 0,0;");
        parameters.setDateDetailing(DateDetailing.HOUR);
//        parameters.setCollectionId(2);
//        parameters.setReplicaId(1);
//        parameters.setDateFrom(new Date(new Date().getTime() - 8000000000L));
//        parameters.setDateTo(new Date());
//        parameters.setTotalTimeMin(10);
//        parameters.setResultCountMin(20);
//        parameters.setOffsetNotZero(true);
//        parameters.setResultCountZero(true);
        List<SearchQueryHistoryPoint> result = searchQueryService.getQueryHistoryTotalTime(parameters);
        result = searchQueryService.getQueryHistoryQueryCount(parameters);
        result = searchQueryService.getQueryHistoryResultCount(parameters);
        result = searchQueryService.getQueryHistoryOffsetNotZeroCount(parameters);
        System.out.println(result.size());
    }

    @Test
    public void getLastParseDateTest(){
        String collectionName = "test_collection_simple_one_server";
        Long replicaNumber = 1l;
        Date lastParseDate = searchQueryResultServiceService.getLastParseDate(collectionName, replicaNumber);

        System.out.println(lastParseDate);
    }

    @Test
    public void getQueryHistoryOffsetNotZeroCountTest(){
        SearchQueryHistorySearchParameters parameters = new SearchQueryHistorySearchParameters();
        parameters.setStart(0);
        parameters.setLimit(10);
        parameters.setCollectionName("test_collection_delta_main_distributed_server");
        parameters.setQuery(" SELECT * FROM test_collection_delta_main_distributed_server LIMIT 0,0;");
        List<SearchQueryHistoryPoint> result = searchQueryService.getQueryHistoryOffsetNotZeroCount(parameters);
        parameters.setDateDetailing(DateDetailing.MINUTE);
        result = searchQueryService.getQueryHistoryOffsetNotZeroCount(parameters);
        System.out.println(result.size());
    }

    @Test
    public void getDistributedCollectionsTest(){
        String collectionName = "test_simp_1";
        List<Collection> result = collectionService.getDistributedCollections(collectionName);
        System.out.println(result.size());
    }

    @Test
    public void getSearchQueryTest(){
        Long collectionId = 2l;
        String searchQueryText = " SELECT * FROM test_collection_delta_main_distributed_server WHERE @id=1;";
        SearchQuery result = searchQueryService.getSearchQuery(collectionId, searchQueryText);
        System.out.println(result);
    }
}

