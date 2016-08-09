package ru.skuptsov.sphinx.console.coordinator.template;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.*;

import java.text.MessageFormat;
import java.util.*;

@Component
public class GenerateSphinxConfService {
	private static final Logger logger = LoggerFactory.getLogger(GenerateSphinxConfService.class);
	
	@Value("${base.dir}")
    private String baseDir;
	
	@Autowired
	private VelocityEngine velocityEngine;

    
    private static final String SQL_BY_TABLE_TEMPLATE = "select {0} from {1}";
	
    private static Map<String, String> reservedWordsToTempReplacements = new HashMap<String, String>();
    
    private static Map<String, String> reservedPhrasesToTempReplacements = new HashMap<String, String>();
	
	static {
		reservedWordsToTempReplacements.put("key", "key_");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1000/1000000::numeric) at time zone 'UTC'", "66666");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1001/1000000::numeric) at time zone 'UTC'", "77777");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1000 / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'", "88888");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1001 / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'", "99999");
		
		reservedPhrasesToTempReplacements.put("to_timestamp(-1000/1000000 - 1)::timestamp without time zone", "44444");
		reservedPhrasesToTempReplacements.put("to_timestamp(-1001/1000000 + 1)::timestamp without time zone", "55555");
		
		
	}
	
	private static Map<String, String> tempReplacementsToReservedWords = new HashMap<String, String>();
	
	private static Map<String, String> tempReplacementsToReservedPhrases = new HashMap<String, String>();
	
	static {
		tempReplacementsToReservedWords.put("key_", "key");
		tempReplacementsToReservedPhrases.put("66666", "to_timestamp($start/1000000::numeric) at time zone 'UTC'");
		tempReplacementsToReservedPhrases.put("77777", "to_timestamp($end/1000000::numeric) at time zone 'UTC'");
		tempReplacementsToReservedPhrases.put("88888", "to_timestamp($start / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'");
		tempReplacementsToReservedPhrases.put("99999", "to_timestamp($end / 1000000 :: NUMERIC) AT TIME ZONE 'UTC'");
		
		tempReplacementsToReservedPhrases.put("44444", "to_timestamp($start/1000000 - 1)::timestamp without time zone");
		tempReplacementsToReservedPhrases.put("55555", "to_timestamp($end/1000000 + 1)::timestamp without time zone");
	}

	public static final String TEMPLATE_LOCATION = "sphinx_conf.vm";

	public String generateContent(Task task, SphinxProcessType sphinxProcessType) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("index_name", task.getProcessName());
		model.put("root_index_name", task.getCollectionName());
		Configuration configuration = null;
		String pathPrefix = "";
	
        CollectionType type = task.getType();
        
        logger.info("COLLECTION TYPE: " + type);
        logger.info("BASE DIR: " + baseDir);
		
		if (sphinxProcessType == SphinxProcessType.SEARCHING) {
			pathPrefix = "searching";
			configuration = task.getSearchConfiguration();
		} else {
			pathPrefix = "indexing";
			configuration = task.getIndexConfiguration();
		}
		
		if (configuration.getSourceConfigurationFields() != null) {
			model.put("sourceConfigurationFields", configuration.getSourceConfigurationFields());
		}

		model.put("collectionType", type);

        if(type == CollectionType.SIMPLE){
            model.put("sourceDeltaConfigurationFields", configuration.getSourceMainConfigurationFields());
        } else if (configuration.getSourceDeltaConfigurationFields() != null) {
			model.put("sourceDeltaConfigurationFields", configuration.getSourceDeltaConfigurationFields());
		}
		
		if (configuration.getSourceMainConfigurationFields() != null) {
			model.put("sourceMainConfigurationFields", configuration.getSourceMainConfigurationFields());
		}
		
		if (configuration.getIndexerConfigurationTemplate() != null) {
		    model.put("indexer", configuration.getIndexerConfigurationTemplate().getConfigurationFields());
		}

		if (configuration.getSearchConfigurationTemplate() != null) {
            Set<ConfigurationFields> searchConfigurationFields = configuration.getSearchConfigurationTemplate().getConfigurationFields();

            if(CollectionType.MAIN_DELTA.equals(task.getType())) {
                boolean isDistThreadsSet = false;
                for (ConfigurationFields searchConfigurationField : searchConfigurationFields) {
                    if ("dist_threads".equals(searchConfigurationField.getFieldKey())) {
                        isDistThreadsSet = true;
                        break;
                    }
                }

                if (!isDistThreadsSet) {
                    ConfigurationFields dist_threads = new ConfigurationFields();
                    dist_threads.setFieldKey("dist_threads");
                    dist_threads.setFieldValue("2");
                    searchConfigurationFields.add(dist_threads);
                }
            }

            ConfigurationFields port = new ConfigurationFields();
            port.setFieldKey("listen");
            port.setFieldValue(task.getSearchConfigurationPort().toString()+":mysql41");
            searchConfigurationFields.add(port);
            
            if (task.getDistributedConfigurationPort() != null) {
	            ConfigurationFields distributedPort = new ConfigurationFields();
	            distributedPort.setFieldKey("listen");
	            distributedPort.setFieldValue(task.getDistributedConfigurationPort().toString());
	            searchConfigurationFields.add(distributedPort);
            }
            
		    model.put("searchd", searchConfigurationFields);
		}

		if (configuration.getConfigurationTemplate() != null) {
		    model.put("index", configuration.getConfigurationTemplate().getConfigurationFields());
		}

		model.put("pathPrefix", pathPrefix);
		model.put("baseDir", baseDir);
		model.put("database", configuration.getDatasource());
		model.put("mappings", configuration.getFieldMappings());
		model.put("idFieldName", idFieldName(configuration.getFieldMappings()));
		Set<FieldMapping> filteredIdFields = filteredIdField(configuration.getFieldMappings());
		model.put("mappingsWithoutIdField", filteredIdFields);
		
		model.put("mappingsWithoutIdFieldForTableCase", filteredIdFieldForTableQuery(configuration.getFieldMappings()));

        model.put("tableName", task.getTableName());
        logger.info("TABLE NAME: " + task.getTableName());
		String mainSqlQuery = task.getTableName() != null ? createSphinxQuery(task.getTableName(), configuration.getFieldMappings()) :task.getMainSqlQuery();
		logger.info("MAIN SQL QUERY: " + mainSqlQuery);
		if (mainSqlQuery != null && !mainSqlQuery.equals("") && task.getTableName() == null) {
            mainSqlQuery = createSphinxQuery(mainSqlQuery, sourceToIndex(configuration.getFieldMappings()));
			model.put("mainSqlQuery", mainSqlQuery);
		}

        String deltaSqlQuery = task.getDeltaSqlQuery();
        logger.info("DELTA SQL QUERY: " + deltaSqlQuery);
        if (deltaSqlQuery != null && !deltaSqlQuery.equals("")) {
            deltaSqlQuery = createSphinxQuery(deltaSqlQuery, sourceToIndex(configuration.getFieldMappings()));
            model.put("deltaSqlQuery", deltaSqlQuery);
        }

		String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, TEMPLATE_LOCATION, "UTF-8",  model);
        logger.info("SPHINX CONF GENERATED CONTENT: " + content);
		return content;
	}
	
	private String idFieldName(Set<FieldMapping> mappings) {
		if (mappings == null) return null;
		for (FieldMapping mapping : mappings) {
		    if (mapping.getIsId()) {
		    	return mapping.getSourceField();
		    }
		}
		return null;
	}
	
	private Set<FieldMapping> filteredIdField(Set<FieldMapping> mappings) {
		if (mappings == null) return null;
		Set<FieldMapping> mappingsWithoutIdField = new HashSet<FieldMapping>();
		for (FieldMapping mapping : mappings) {
		    if (!mapping.getIsId() && mapping.getIndexFieldType() != IndexFieldType.SQL_FIELD) {
		    	mappingsWithoutIdField.add(mapping);  
		    }
		}
		return mappingsWithoutIdField;
	}
	
	private Set<FieldMapping> filteredIdFieldForTableQuery(Set<FieldMapping> mappings) {
		if (mappings == null) return null;
		Set<FieldMapping> mappingsWithoutIdField = new HashSet<FieldMapping>();
		for (FieldMapping mapping : mappings) {
		    if (!mapping.getIsId()) {
		    	mappingsWithoutIdField.add(mapping);  
		    }
		}
		return mappingsWithoutIdField;
	}
	
	private Map<String, String> sourceToIndex(Set<FieldMapping> mappings) {
	    Map<String, String> sourceToIndex = new HashMap<String, String>();
	    
	    
	    for (FieldMapping mapping : mappings) {
	    	sourceToIndex.put(mapping.getSourceField().toLowerCase(), mapping.getIndexField());	
	    }
	    
	    return sourceToIndex;
	}

    public String createSphinxQuery(String tableName, final LinkedHashSet<FieldMapping> fieldMappings) {
        String fieldMappingsString = "";
        if(fieldMappings.size() > 0) {
            fieldMappingsString = idFieldName(fieldMappings);
            for (FieldMapping fieldMapping : filteredIdFieldForTableQuery(fieldMappings)) {
                fieldMappingsString += ", " + fieldMapping.getSourceField();
            }
        }
        return MessageFormat.format(SQL_BY_TABLE_TEMPLATE, fieldMappingsString, tableName);
    }

	private String createSphinxQuery(String sqlQuery, final Map<String, String> sourceToIndex) {
		Select select = null;
	
		try {
			
			for (String key : reservedWordsToTempReplacements.keySet()) {
				sqlQuery = sqlQuery.replaceAll("\\b" + key + "\\b", reservedWordsToTempReplacements.get(key));
			}
			
			sqlQuery = sqlQuery.replace("$start", "-1000");
			sqlQuery = sqlQuery.replace("$end", "-1001");
			
			for (String key : reservedPhrasesToTempReplacements.keySet()) {
				sqlQuery = sqlQuery.replace(key, reservedPhrasesToTempReplacements.get(key));
			}
			
			select = (Select) CCJSqlParserUtil.parse(sqlQuery);
		} catch (JSQLParserException e) {
			logger.error("ERROR OCCURED WHILE PARSING SQL: ", e);
			logger.info("SQL: " + sqlQuery);
			throw new ApplicationException(e);
		}
		
		
		// start processing
		select.getSelectBody().accept(new SelectVisitor() {

		    @Override
		    public void visit(PlainSelect plain) {
		      
		    	for (final SelectItem item: plain.getSelectItems()) {
					 
					item.accept(new SelectItemVisitorAdapter() {
						private String columnName = null;
						
						@Override
						public void visit(SelectExpressionItem column) {
							
							columnName = column.getAlias() != null ?column.getAlias().getName():null;
							
							column.getExpression().accept(new ExpressionVisitorAdapter() {
								@Override
								public void visit(Column column) {
									if (columnName == null) {
									    columnName = column.getColumnName();
									}
								}
								
		                        public void visit(CaseExpression caseExpression) {
									logger.info("CASE EXPRESSION: " + caseExpression);
								}

							});
							
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
							if (prefix != null)
						        column.setAlias(new Alias(prefix));	 	
							
							
						}
						
					});
				}
	
		    }

		    @Override
		    public void visit(SetOperationList sol) {
		        logger.info("visitor:" + sol.getPlainSelects());
		        logger.info("visitor:" + sol.getOperations());
		        
		        List<PlainSelect> plainSelects = sol.getPlainSelects();
		        
		        for (PlainSelect plain : plainSelects) {
		        	visit(plain);
		        }
		    }

		    @Override
		    public void visit(WithItem wi) {
		        
		    }
		});
		// end processing
		
         
        String finalSql = select.toString();
        for (String key : tempReplacementsToReservedWords.keySet()) {
			finalSql = finalSql.replaceAll("\\b" + key + "\\b", tempReplacementsToReservedWords.get(key));
		}
        
        finalSql = finalSql.replace("-1000", "$start");
		finalSql = finalSql.replace("-1001", "$end");
		
		for (String key : tempReplacementsToReservedPhrases.keySet()) {
			finalSql = finalSql.replace(key, tempReplacementsToReservedPhrases.get(key));
		}
		
		try {
		    finalSql = new SqlFormatter().format(finalSql);
		} catch (Throwable e) {
			logger.error("error occured: ", e);
		}
		
		return finalSql;
	}

}
