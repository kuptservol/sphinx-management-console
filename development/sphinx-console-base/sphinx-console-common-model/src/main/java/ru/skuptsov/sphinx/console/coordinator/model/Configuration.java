package ru.skuptsov.sphinx.console.coordinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Configuration extends BaseEntity {
	private Long id;
    private String name;
    private String filePath;
    private ConfigurationTemplate configurationTemplate;
    private ConfigurationTemplate searchConfigurationTemplate;
    private ConfigurationTemplate indexerConfigurationTemplate;
    @Valid
    private DataSource datasource;
    @Valid
    @JsonDeserialize(as=LinkedHashSet.class)
    private Set<FieldMapping> fieldMappings = new LinkedHashSet<FieldMapping>();
    @Valid
    @JsonDeserialize(as=LinkedHashSet.class)
    private Set<ConfigurationFields> sourceConfigurationFields = new HashSet<ConfigurationFields>();
    @Valid
    @JsonDeserialize(as=LinkedHashSet.class)
    private Set<ConfigurationFields> searchConfigurationFields = new HashSet<ConfigurationFields>();
    
    
    private final static String SEARCH_PORT_FIELD_KEY = "listen";
    private final static String SQL_QUERY_FIELD_KEY = "sql_query";
    private final static String DISTRIBUTED_PORT_FIELD_KEY = "distributed_listen";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ConfigurationTemplate getConfigurationTemplate() {
        return configurationTemplate;
    }

    public void setConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        this.configurationTemplate = configurationTemplate;
    }

    public ConfigurationTemplate getSearchConfigurationTemplate() {
        return searchConfigurationTemplate;
    }

    public void setSearchConfigurationTemplate(ConfigurationTemplate searchConfigurationTemplate) {
        this.searchConfigurationTemplate = searchConfigurationTemplate;
    }

    public ConfigurationTemplate getIndexerConfigurationTemplate() {
        return indexerConfigurationTemplate;
    }

    public void setIndexerConfigurationTemplate(ConfigurationTemplate indexerConfigurationTemplate) {
        this.indexerConfigurationTemplate = indexerConfigurationTemplate;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

	public LinkedHashSet<FieldMapping> getFieldMappings() {
		return (fieldMappings != null) ? new LinkedHashSet<FieldMapping>(fieldMappings): null;
	}

	@JsonDeserialize(as=LinkedHashSet.class)
	public void setFieldMappings(Set<FieldMapping> fieldMappings) {
		if (fieldMappings != null) {
			this.fieldMappings = new LinkedHashSet<FieldMapping>(fieldMappings);
		} else {
		    this.fieldMappings = fieldMappings;
		}
	}

	public Set<ConfigurationFields> getSourceConfigurationFields() {
		return sourceConfigurationFields;
	}

	@JsonDeserialize(as=LinkedHashSet.class)
	public void setSourceConfigurationFields(Set<ConfigurationFields> sourceConfigurationFields) {
        this.sourceConfigurationFields = sourceConfigurationFields;
	}
	
	@JsonIgnore
    public Set<ConfigurationFields> getSourceDeltaConfigurationFields() {
		Set<ConfigurationFields> sourceDeltaConfigurationFields = new HashSet<ConfigurationFields>();
		
		for (ConfigurationFields field  : sourceConfigurationFields) {
			if (field.getIndexType() == IndexType.DELTA) {
				sourceDeltaConfigurationFields.add(field);    	
			}
		}
		
		return sourceDeltaConfigurationFields;
	}

	
	@JsonIgnore
	public Set<ConfigurationFields> getSourceMainConfigurationFields() {
		Set<ConfigurationFields> sourceMainConfigurationFields = new HashSet<ConfigurationFields>();
		
		for (ConfigurationFields field  : sourceConfigurationFields) {
			if (field.getIndexType() == IndexType.MAIN) {
				sourceMainConfigurationFields.add(field);    	
			}
		}
		
		return sourceMainConfigurationFields;
	}

	

	public Set<ConfigurationFields> getSearchConfigurationFields() {
        return searchConfigurationFields;
    }

	@JsonDeserialize(as=LinkedHashSet.class)
	public void setSearchConfigurationFields(Set<ConfigurationFields> searchConfigurationFields) {
        this.searchConfigurationFields = searchConfigurationFields;
    }
	
	 @JsonIgnore
	 public Integer getDistributedListenPort() {
	     for(ConfigurationFields configurationField : this.getSearchConfigurationFields()) {
	            if(configurationField.getFieldKey().equals(DISTRIBUTED_PORT_FIELD_KEY)) {
	                return new Integer(configurationField.getFieldValue());
	            }
	     }
	     return null;
	 }


    //TODO may be it should be in hibernate mapping
    @JsonIgnore
    public String getSearchListenPort() {
        for(ConfigurationFields configurationField : this.getSearchConfigurationFields()) {
            if(configurationField.getFieldKey().equals(SEARCH_PORT_FIELD_KEY)) {
                return configurationField.getFieldValue();
            }
    }
        return null;
    }

    @JsonIgnore
    public ConfigurationFields getMainSqlQueryField() {
        for(ConfigurationFields configurationField : this.getSourceConfigurationFields()) {
            if(configurationField.getFieldKey().equals(SQL_QUERY_FIELD_KEY) && configurationField.getIndexType() == IndexType.MAIN) {
                return configurationField;
            }
        }
        return null;
    }

    @JsonIgnore
    public ConfigurationFields getDeltaSqlQueryField() {
        for(ConfigurationFields configurationField : this.getSourceConfigurationFields()) {
            if(configurationField.getFieldKey().equals(SQL_QUERY_FIELD_KEY)  && configurationField.getIndexType() == IndexType.DELTA) {
                return  configurationField;
            }
        }
        return null;
    }

    @JsonIgnore
    public String getMainSqlQuery() {
        ConfigurationFields sqlQueryField = getMainSqlQueryField();
        return sqlQueryField != null ? sqlQueryField.getFieldValue() : null;
    }

    @JsonIgnore
    public String getDeltaSqlQuery() {
        ConfigurationFields sqlQueryField = getDeltaSqlQueryField();
        return sqlQueryField != null ? sqlQueryField.getFieldValue() : null;
    }
    
    @JsonIgnore
    public List<FieldMapping> getSqlFields() {
    	List<FieldMapping> fields = new ArrayList<FieldMapping>();
    	if (this.fieldMappings != null) {
    		for (FieldMapping fieldMapping : this.fieldMappings) {
    			if (fieldMapping.getIndexFieldType() == IndexFieldType.SQL_FIELD) {
    				fields.add(fieldMapping);
    			}
    		}
    	}
    	return fields;
    }

    public void setSourceConfigurationFieldsConfiguration(){
        for(ConfigurationFields configurationFields : getSourceConfigurationFields()){
            configurationFields.setConfiguration(this);
        }
    }

    public void setSearchConfigurationFieldsConfiguration(){
        for(ConfigurationFields configurationFields : getSearchConfigurationFields()){
            configurationFields.setConfiguration(this);
        }
    }
}
