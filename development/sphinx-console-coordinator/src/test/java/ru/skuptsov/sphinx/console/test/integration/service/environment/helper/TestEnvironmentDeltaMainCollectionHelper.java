package ru.skuptsov.sphinx.console.test.integration.service.environment.helper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import ru.skuptsov.sphinx.console.OrderedSpringJUnit4ClassRunner;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.model.IndexFieldType;
import ru.skuptsov.sphinx.console.test.integration.service.SpringPropertiesUtil;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Andrey on 29.01.2015.
 */
@RunWith(OrderedSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-delta-main-context.xml"})
public class TestEnvironmentDeltaMainCollectionHelper extends TestEnvironmentHelper {

    public static final String REPAIR_SUFFIX = "_repair";
    public Set<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();

    @Before
    public void before() throws SQLException, ClassNotFoundException{
        super.before();
        initFields();
    }

    @Value("${admin.jar.file.path}")
    public String adminJarFilePath;

    @Value("${admin.changesets.path}")
    public String adminChangesetsPath;

    @Value("${admin.config.location}")
    public String adminConfigLocation;

    @Value("${admin.test.properties.path}")
    public String adminTestPropertiesPath;

    @Value("${delta.main.collection.name}")
    public String deltaMainCollectionName;

    @Value("${replica1.search.port}")
    public int replica1SearchPort;

    @Value("${replica1.distributed.search.port}")
    public int replica1DistributedSearchPort;

    @Value("${repair.delta.main.index.port}")
    public int repairDeltaMainIndexPort;

    @Value("${repair.delta.main.distributed.index.port}")
    public int repairDeltaMainDistributedIndexPort;

    @Value("${replica2.search.port}")
    public int replica2SearchPort;

    @Value("${replica2.distributed.search.port}")
    public int replica2DistributedSearchPort;

    @Value("${replica3.search.port}")
    public int replica3SearchPort;

    @Value("${replica3.distributed.search.port}")
    public int replica3DistributedSearchPort;

    @Value("${delta.main.deltasql.query}")
    public String deltaMainDeltaSql;

    @Value("${delta.main.mainsql.query}")
    public String deltaMainMainSql;

    @Value("${delta.main.datasource.table}")
    public String deltaMainTable;

    @Value("${delta.main.datasource.port}")
    public int deltaMainDataSourcePort;

    @Value("${delta.main.datasource.type}")
    public String deltaMainDataSourceType;

    @Value("${delta.main.datasource.host}")
    public String deltaMainDataSourceHost;

    @Value("${delta.main.datasource.username}")
    public String deltaMainDataSourceUsername;

    @Value("${delta.main.datasource.password}")
    public String deltaMainDataSourcePassword;

    @Value("${delta.main.datasource.db}")
    public String deltaMainDatasourceDB;

    @Value("${delta.main.external.action.code}")
    public String deltaMainExternalActionCode;

    @Value("${delta.main.main.sql_query_range}")
    public String deltaMainMainSqlQueryRange;

    @Value("${delta.main.main.sql_range_step}")
    public String deltaMainMainSqlRangeStep;

    @Value("${delta.main.id.to.find.value}")
    public Long deltaMainIdToFindValue;

    @Value("${delta.main.expected.value.part}")
    public String deltaMainExpectedValue;

    @Value("${delta.main.delta.sql_query_pre}")
    public String deltaMainDeltaSqlQueryPre;

    @Value("${delta.main.delta.sql_query_range}")
    public String deltaMainDeltaSqlQueryRange;

    @Value("${delta.main.delta.sql_query_post_index}")
    public String deltaMainDeltaSqlQueryPostIndex;

    @Value("${delta.main.delta.sql_range_step}")
    public String deltaMainDeltaSqlQueryRangeStep;

    @Value("${delta.main.delete.scheme.request}")
    public String deleteSchemeRequest;

    @Value("${field.additional.for.full.indexing}")
    public String fieldAdditionalForFullIndexing;

    @Value("${cron.main}")
    public String cronMain;

    @Value("${cron.delta}")
    public String cronDelta;

    public void initFields(){

        String name = SpringPropertiesUtil.getProperty("field.id.name");
        IndexFieldType type = IndexFieldType.SQL_ATTR_UINT;
        int fieldsCount = Integer.parseInt(SpringPropertiesUtil.getProperty("fields.count"));
        FieldMapping fieldMapping = new FieldMapping();

        fieldMapping.setIndexField(name);
        fieldMapping.setSourceField(name);
        fieldMapping.setIndexFieldType(type);
        fieldMapping.setIsId(true);

        fieldMappings.add(fieldMapping);

        for(int i = 2; i <= fieldsCount; i++){
            fieldMapping = new FieldMapping();
            name = SpringPropertiesUtil.getProperty("field" + i + ".name");
            type = IndexFieldType.getByTitle(SpringPropertiesUtil.getProperty("field" + i + ".type"));
            fieldMapping.setIndexField(name);
            fieldMapping.setSourceField(name);
            fieldMapping.setIndexFieldType(type);
            fieldMapping.setIsId(false);
            fieldMappings.add(fieldMapping);
        }

    }
}
