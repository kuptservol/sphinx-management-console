package ru.skuptsov.sphinx.console.coordinator.template;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.task.AddCollectionTask;

import java.util.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:sphinx.console-coordinator-generate-sphinx-conf-context-test.xml"})

public class GenerateSphinxConfServiceTest {
	//@Autowired
    private GenerateSphinxConfService generateSphinxConfService;
	
	
	private static Map<String, String> sourceToIndex = new HashMap<String, String>();
	
	static {
		sourceToIndex.put("key", "key1");
		sourceToIndex.put("deleted", "AAA");
		//sourceToIndex.put("ppp", "P1");
		//sourceToIndex.put("type", "type3");
		//sourceToIndex.put("port", "port4");
	}
	
	private static Map<String, String> reservedWordsToTempReplacements = new HashMap<String, String>();
	
	static {
		reservedWordsToTempReplacements.put("key", "key_");
	}
	
	private static Map<String, String> tempReplacementsToReservedWords = new HashMap<String, String>();
	
	static {
		tempReplacementsToReservedWords.put("key_", "key");
	}
	
	//@Test
	public void hSqlParserTest() {
		Select select = null;
		try {
			//select max(s.server_id) as id, s.name as name, a.type as type, a.port as port from sphinx.console.SERVER s, sphinx.console.ADMIN_PROCESS a where s.server_id = a.server_id
			//select max(s.server_id), s.name, a.type, a.port from sphinx.console.SERVER s, sphinx.console.ADMIN_PROCESS a where s.server_id = a.server_id
			
			
		//	String sql = "select r.id as id, r.bucket as bucket, r.key as key, read_riakcs2(r.bucket, r.key) as data  from riakdata r limit 1";
			
			String sql = "select sh.*, (CASE WHEN sul.operation='D' THEN 1 ELSE 0 END) as deleted from zsrchhmsm.search_update_list sul join zsrchhmsm.search_houses sh on sul.object_id = sh.id";
			
			for (String key : reservedWordsToTempReplacements.keySet()) {
			    sql = sql.replaceAll("\\b" + key + "\\b", reservedWordsToTempReplacements.get(key));
			}
			
			System.out.println("SQL: " + sql);
			
			select = (Select) CCJSqlParserUtil.parse(sql);
			System.out.println("SELECT: " + select);
		} catch (JSQLParserException e) {
			e.printStackTrace();
			
		}
		
		PlainSelect plain = (PlainSelect)select.getSelectBody();     

	
		for (final SelectItem item: plain.getSelectItems()) {
			 
			item.accept(new SelectItemVisitorAdapter() {
				private String columnName = null;
				
				@Override
				public void visit(SelectExpressionItem column) {
					
					System.out.println("EXPRESSION: " + column.getExpression().getClass());
					column.getExpression().accept(new ExpressionVisitorAdapter() {
						@Override
						public void visit(Column column) {
							
							System.out.println("COLUMN: " + column);
							
							columnName = column.getColumnName();
						}
						
						public void visit(CaseExpression caseExpression) {
							
							System.out.println("CASE EXPRESSION: " + caseExpression);
							
						}



					});
					
					System.out.println(columnName);
					String prefix = null;
					if (columnName != null) {
						String realName = tempReplacementsToReservedWords.get(columnName.toLowerCase());
						String name = null;
						if (realName != null) {
							name = realName;
						} else {
							name = columnName.toLowerCase();
						}
					    prefix = sourceToIndex.get(name);
					    if (prefix == null) {
					    	if (column.getAlias() != null)
					    	    prefix = sourceToIndex.get(column.getAlias().getName().toLowerCase());
					    }
					} else {
						if (column.getAlias() != null)
						    prefix = sourceToIndex.get(column.getAlias().getName().toLowerCase());
					}
					
					if (prefix != null) {
				        column.setAlias(new Alias(prefix));
					} /*else {
						if (column.getAlias() != null) {
							column.setAlias(null);
						}
					}*/
					
					
				}
				
			});
		}
		
		
		
		
		/*for (WithItem item : items) {
			System.out.println("ITEM: " + item);
		}*/
		
	//=	select.getSelectBody()
		
		String finalSql = select.toString();
		
		for (String key : tempReplacementsToReservedWords.keySet()) {
			finalSql = finalSql.replaceAll("\\b" + key + "\\b", tempReplacementsToReservedWords.get(key));
		}
		
		System.out.println("FINAL SELECT: " + finalSql);
	}
	
	//@Test
	//@Ignore
	public void generateSphinxConfTest() {
		AddCollectionTask task = new AddCollectionTask();
		Collection collection = new Collection();
		collection.setName("collection1");
		task.setCollection(collection);
		
		Configuration searchConfiguration = new Configuration();
        
        searchConfiguration.setName("CCC search 222");
        
        ConfigurationTemplate indexConfigurationTemplate = new ConfigurationTemplate();
        indexConfigurationTemplate.setDefaultTemplate(true);
        indexConfigurationTemplate.setSystemTemplate(false);
        indexConfigurationTemplate.setName("template2");
        indexConfigurationTemplate.setDescription("configurationTemplate2");
        indexConfigurationTemplate.setType(ConfigurationType.CONFIGURATION);
        
        Set<ConfigurationFields> indexConfigurationFields = new HashSet<ConfigurationFields>();
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
        
        Set<ConfigurationFields> searchConfigurationFields = new HashSet<ConfigurationFields>();
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
        
        Set<ConfigurationFields> configurationFields = new HashSet<ConfigurationFields>();
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
        
        DataSource dataSource = new DataSource();
        dataSource.setPort(3306);
        dataSource.setHost("192.168.211.111");
        dataSource.setSqlDb("sphinx.console");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setType(DataSourceType.MYSQL);
        
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
        
		task.setSearchConfiguration(searchConfiguration);
        
		generateSphinxConfService.generateContent(task, SphinxProcessType.SEARCHING);	
	}
}
