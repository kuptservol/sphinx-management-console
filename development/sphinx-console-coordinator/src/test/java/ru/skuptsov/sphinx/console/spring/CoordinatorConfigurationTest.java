package ru.skuptsov.sphinx.console.spring;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.Predicate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:sphinx.console-hibernate-context-test.xml"})
public class CoordinatorConfigurationTest {

    public static final String SERVER_URI = "http://192.168.253.129:8080/sphinx.console-coordinator";
    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private ServerService serverService;

    @Ignore
    @Test
    public void successfulTest() {
        Status status = REST_TEMPLATE.getForObject(SERVER_URI+ CoordinatorConfigurationRestURIConstants.TEST, Status.class);
        System.out.println(status.getCode());
        Assert.assertEquals(0, status.getCode());
    }

    @Test
    public void addServerTest() {
        Server server = new Server();
        server.setId(3L);
        server.setName("server2");
        server.setDomain("sdfsdfsdf");
        server.setIp("0.0.0.0");
        ResponseEntity<Status> responseEntity = REST_TEMPLATE.postForEntity(SERVER_URI + CoordinatorConfigurationRestURIConstants.ADD_SERVER, server, Status.class);
        System.out.println(responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
    }

    @Ignore
    @Test
    public void deleteServerTest() {
        REST_TEMPLATE.delete(SERVER_URI + CoordinatorConfigurationRestURIConstants.DELETE_SERVER, 3L);
        Assert.assertTrue(Boolean.TRUE);
    }

    @Ignore
    @Test
    public void addAdminProcessTest() {
        Server[] servers = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.SERVERS, Server[].class);
        AdminProcess adminProcess = new AdminProcess();
        adminProcess.setType(ProcessType.SEARCH_AGENT);
        adminProcess.setPort(4567);
        adminProcess.setServer(servers[0]);
        ResponseEntity<Status> responseEntity = REST_TEMPLATE.postForEntity(SERVER_URI + CoordinatorConfigurationRestURIConstants.ADD_ADMIN_PROCESS, adminProcess, Status.class);
        System.out.println(responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
    }

    @Ignore
    @Test
    public void updateAdminProcessTest() {
        Server[] servers = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.SERVERS, Server[].class);
        AdminProcess adminProcess = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.ADMIN_PROCESS, AdminProcess.class, 1);
        adminProcess.setType(ProcessType.COORDINATOR);
        adminProcess.setServer(servers[1]);
        REST_TEMPLATE.put(SERVER_URI + CoordinatorConfigurationRestURIConstants.UPDATE_ADMIN_PROCESS, adminProcess);
        Assert.assertEquals(adminProcess.getType(), ProcessType.COORDINATOR);
        Assert.assertEquals(adminProcess.getServer().getId(), servers[1].getId());
    }

    @Ignore
    @Test
    public void getAdminProcessTest() {
        Server[] servers = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.SERVERS, Server[].class);
        AdminProcess adminProcess = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.ADMIN_PROCESS, AdminProcess.class, 1);
        adminProcess.setType(ProcessType.COORDINATOR);
        adminProcess.setServer(servers[1]);
        REST_TEMPLATE.put(SERVER_URI + CoordinatorConfigurationRestURIConstants.UPDATE_ADMIN_PROCESS, adminProcess);
        Assert.assertEquals(adminProcess.getType(), ProcessType.COORDINATOR);
        Assert.assertEquals(adminProcess.getServer().getId(), servers[1].getId());
    }

    @Ignore
    @Test
    public void deleteAdminProcessTest() {
        REST_TEMPLATE.delete(SERVER_URI + CoordinatorConfigurationRestURIConstants.DELETE_ADMIN_PROCESS, 4L);
        Assert.assertTrue(Boolean.TRUE);
    }

    private void createConfiguration(Configuration configuration, DataSource dataSource, String listenPort, String sqlQuery) {
        ConfigurationTemplate[] searchConfigurationTemplates = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.SEARCH_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertTrue(searchConfigurationTemplates.length > 1);

        configuration.setSearchConfigurationTemplate(searchConfigurationTemplates[0]);

        ConfigurationTemplate[] indexConfigurationTemplates = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.INDEXER_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertTrue(indexConfigurationTemplates.length > 1);

        configuration.setIndexerConfigurationTemplate(indexConfigurationTemplates[0]);

        ConfigurationTemplate[] configurationConfigurationTemplates = REST_TEMPLATE.getForObject(SERVER_URI + CoordinatorViewRestURIConstants.CONFIGURATION_TEMPLATES, ConfigurationTemplate[].class);
        Assert.assertTrue(configurationConfigurationTemplates.length > 1);

        configuration.setConfigurationTemplate(configurationConfigurationTemplates[0]);

        configuration.setDatasource(dataSource);

        FieldMapping fieldMapping1 = new FieldMapping();
        fieldMapping1.setIndexField("id");
        fieldMapping1.setSourceField("id");
        fieldMapping1.setIndexFieldCommentary("id");
        fieldMapping1.setIndexFieldType(IndexFieldType.SQL_ATTR_BIGINT);
        fieldMapping1.setIsId(true);

        FieldMapping fieldMapping2 = new FieldMapping();
        fieldMapping2.setIndexField("text_data");
        fieldMapping2.setSourceField("text_data");
        fieldMapping2.setIndexFieldCommentary("text_data");
        fieldMapping2.setIndexFieldType(IndexFieldType.SQL_FIELD_STRING);
        fieldMapping2.setIsId(false);

        LinkedHashSet<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();
        fieldMappings.add(fieldMapping1);
        fieldMappings.add(fieldMapping2);

        configuration.setFieldMappings(fieldMappings);
    }
    
    private void createConfiguration(Configuration searchConfiguration, DataSource dataSource) {
    	searchConfiguration.setDatasource(dataSource);
        
        ConfigurationTemplate indexConfigurationTemplate = new ConfigurationTemplate();
        indexConfigurationTemplate.setDefaultTemplate(true);
        indexConfigurationTemplate.setSystemTemplate(false);
        indexConfigurationTemplate.setName("template2");
        indexConfigurationTemplate.setDescription("configurationTemplate2");
        indexConfigurationTemplate.setType(ConfigurationType.CONFIGURATION);
        
        SortedSet<ConfigurationFields> indexConfigurationFields = new TreeSet<ConfigurationFields>();
        ConfigurationFields indexConfigurationField = new ConfigurationFields();
        indexConfigurationField.setFieldKey("mem_limit");
        indexConfigurationField.setFieldValue("32M");
        
        
        indexConfigurationFields.add(indexConfigurationField);
        
        indexConfigurationTemplate.setConfigurationFields(indexConfigurationFields);
        
        searchConfiguration.setIndexerConfigurationTemplate(indexConfigurationTemplate);
        
        
        
        ConfigurationTemplate searchConfigurationTemplate = new ConfigurationTemplate();
        searchConfigurationTemplate.setDefaultTemplate(true);
        searchConfigurationTemplate.setSystemTemplate(false);
        searchConfigurationTemplate.setName("template2");
        searchConfigurationTemplate.setDescription("configurationTemplate2");
        searchConfigurationTemplate.setType(ConfigurationType.SEARCH);
        
        SortedSet<ConfigurationFields> searchConfigurationFields = new TreeSet<ConfigurationFields>();
        ConfigurationFields searchConfigurationField1 = new ConfigurationFields();
        searchConfigurationField1.setFieldKey("listen");
        searchConfigurationField1.setFieldValue("9312");
        
        ConfigurationFields searchConfigurationField2 = new ConfigurationFields();
        searchConfigurationField2.setFieldKey("listen");
        searchConfigurationField2.setFieldValue("9306:mysql41");
        
        ConfigurationFields searchConfigurationField3 = new ConfigurationFields();
        searchConfigurationField3.setFieldKey("log");
        searchConfigurationField3.setFieldValue("test");
        
        ConfigurationFields searchConfigurationField4 = new ConfigurationFields();
        searchConfigurationField4.setFieldKey("query_log");
        searchConfigurationField4.setFieldValue("test");
        
        ConfigurationFields searchConfigurationField5 = new ConfigurationFields();
        searchConfigurationField5.setFieldKey("pid_file");
        searchConfigurationField5.setFieldValue("test");
        
        ConfigurationFields searchConfigurationField6 = new ConfigurationFields();
        searchConfigurationField6.setFieldKey("binlog_path");
        searchConfigurationField6.setFieldValue("test");
        
        
        
        searchConfigurationFields.add(searchConfigurationField1);
        searchConfigurationFields.add(searchConfigurationField2);
        searchConfigurationFields.add(searchConfigurationField3);
        searchConfigurationFields.add(searchConfigurationField4);
        searchConfigurationFields.add(searchConfigurationField5);
        searchConfigurationFields.add(searchConfigurationField6);
        
        searchConfigurationTemplate.setConfigurationFields(searchConfigurationFields);
       
        
        searchConfiguration.setSearchConfigurationTemplate(searchConfigurationTemplate);
        
        
        //index template
        ConfigurationTemplate configurationTemplate = new ConfigurationTemplate();
        configurationTemplate.setDefaultTemplate(true);
        configurationTemplate.setSystemTemplate(false);
        configurationTemplate.setName("template2");
        configurationTemplate.setDescription("configurationTemplate2");
        configurationTemplate.setType(ConfigurationType.INDEX);
        
        SortedSet<ConfigurationFields> configurationFields = new TreeSet<ConfigurationFields>();
        ConfigurationFields configurationField1 = new ConfigurationFields();
        configurationField1.setFieldKey("path");
        configurationField1.setFieldValue("test");
        
        ConfigurationFields configurationField2 = new ConfigurationFields();
        configurationField2.setFieldKey("charset_type");
        configurationField2.setFieldValue("sbcs");
        
        
        ConfigurationFields configurationField3 = new ConfigurationFields();
        configurationField3.setFieldKey("docinfo");
        configurationField3.setFieldValue("extern");
        
        
        
        configurationFields.add(configurationField1);
        configurationFields.add(configurationField2);
        configurationFields.add(configurationField3);
        
        configurationTemplate.setConfigurationFields(configurationFields);
       
        
        searchConfiguration.setConfigurationTemplate(configurationTemplate);
        //
        
        
        searchConfiguration.setDatasource(dataSource);
        
        FieldMapping fieldMapping1 = new FieldMapping();
        fieldMapping1.setIndexField("SERVER_ID");
        fieldMapping1.setSourceField("SERVER_ID");
        fieldMapping1.setIndexFieldCommentary("SERVER_ID");
        fieldMapping1.setIndexFieldType(IndexFieldType.SQL_ATTR_BIGINT);
        fieldMapping1.setIsId(true);
        
        FieldMapping fieldMapping2 = new FieldMapping();
        fieldMapping2.setIndexField("IP");
        fieldMapping2.setSourceField("IP");
        fieldMapping2.setIndexFieldCommentary("IP");
        fieldMapping2.setIndexFieldType(IndexFieldType.SQL_FIELD_STRING);
        fieldMapping2.setIsId(false);
        
        FieldMapping fieldMapping3 = new FieldMapping();
        fieldMapping3.setIndexField("DOMAIN_NAME");
        fieldMapping3.setSourceField("DOMAIN_NAME");
        fieldMapping3.setIndexFieldCommentary("DOMAIN_NAME");
        fieldMapping3.setIndexFieldType(IndexFieldType.SQL_FIELD_STRING);
        fieldMapping3.setIsId(false);
        
        FieldMapping fieldMapping4 = new FieldMapping();
        fieldMapping4.setIndexField("NAME");
        fieldMapping4.setSourceField("NAME");
        fieldMapping4.setIndexFieldCommentary("NAME");
        fieldMapping4.setIndexFieldType(IndexFieldType.SQL_FIELD_STRING);
        fieldMapping4.setIsId(false);

        LinkedHashSet<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();
        fieldMappings.add(fieldMapping1);
        fieldMappings.add(fieldMapping2);
        fieldMappings.add(fieldMapping3);
        fieldMappings.add(fieldMapping4);
        
        searchConfiguration.setFieldMappings(fieldMappings);

        
    }

    private Server findServerByIp(final String ip) {
        List<Server> servers = serverService.getServers();
        CollectionUtils.filter(servers, new Predicate() {
            @Override
            public boolean evaluate(Object server) {
                return ((Server) server).getIp().equals(ip);
            }
        });
        return servers.size() > 0 ? servers.get(0) : null;
    }

    @Ignore
    @Test
    public void addCollectionTest() {
        String collectionName = "collection1";
        CollectionWrapper collectionWrapper = new CollectionWrapper();
        
        CronScheduleWrapper cronSchedule = new CronScheduleWrapper();
        cronSchedule.setCronSchedule("0 15 10 ? * *");
        collectionWrapper.setCronSchedule(cronSchedule);

        String ip = "192.168.187.200";
        Server server = findServerByIp(ip);
        Server searchServer = server != null ?  server : new Server();
        searchServer.setIp(ip);
        searchServer.setName(server != null ?  server.getName() : "agent_server");
        collectionWrapper.setSearchServer(searchServer);

        collectionWrapper.setIndexServer(searchServer);

        Collection collection = new Collection();
        collection.setName(collectionName);
        collection.setType(CollectionType.SIMPLE);
        collection.setDescription("description " + collectionName);
        collectionWrapper.setCollection(collection);

        DataSource dataSource = new DataSource();
        dataSource.setPort(3306);
        dataSource.setHost("192.168.211.111");
        dataSource.setSqlDb("sphinx.console");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setType(DataSourceType.MYSQL);

        Delta delta = new Delta();
        delta.setType(DeltaType.DELTA);
        collection.setDelta(delta);

        final Integer listenPort = 7777;
        String sqlQuery = "";
        Configuration searchConfiguration = new Configuration();
        searchConfiguration.setName("search-conf-" + collectionName + "-1");
        createConfiguration(searchConfiguration, dataSource, listenPort.toString(), sqlQuery);

        collectionWrapper.setSearchConfiguration(searchConfiguration);

        Configuration indexConfiguration = new Configuration();
        indexConfiguration.setName("index-conf-" + collectionName);
        createConfiguration(indexConfiguration, dataSource, listenPort.toString(), sqlQuery);

        collectionWrapper.setSearchConfigurationPort(new SearchConfigurationPortWrapper(){{setSearchConfigurationPort(listenPort);}});

        collectionWrapper.setIndexConfiguration(indexConfiguration);

        ResponseEntity<Status> responseEntity = REST_TEMPLATE.postForEntity(SERVER_URI + CoordinatorConfigurationRestURIConstants.ADD_COLLECTION, collectionWrapper, Status.class);
        System.out.println(responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());
    }

    @Ignore
    @Test
    public void createReplicaTest() {
        Collection collection = collectionService.findById(1L);
        Server server = serverService.getServer(2L);
        Replica replica = new Replica();
        replica.setCollection(collection);
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        replicaWrapper.setSearchPort(1111);
        replicaWrapper.setServer(server);
        replicaWrapper.setCollectionName(collection.getName());
        ResponseEntity<Status> status = REST_TEMPLATE.postForEntity(SERVER_URI + CoordinatorConfigurationRestURIConstants.CREATE_REPLICA, replicaWrapper, Status.class);
        Assert.assertTrue(Boolean.TRUE);
    }

    @Ignore
    @Test
    public void deleteReplicaTest() {
        ReplicaWrapper replicaWrapper = new ReplicaWrapper();
        replicaWrapper.setCollectionName("collection1");
        replicaWrapper.setReplicaNumber(2L);
        ResponseEntity<Status> status = REST_TEMPLATE.postForEntity(SERVER_URI + CoordinatorConfigurationRestURIConstants.REMOVE_REPLICA, replicaWrapper, Status.class);
        Assert.assertTrue(Boolean.TRUE);
    }
}
