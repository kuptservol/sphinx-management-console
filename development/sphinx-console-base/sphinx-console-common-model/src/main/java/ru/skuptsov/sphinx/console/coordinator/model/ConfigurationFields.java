package ru.skuptsov.sphinx.console.coordinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ConfigurationFields extends BaseEntity {
	private Long id;
    @NotEmpty
    private String fieldKey;
    @NotEmpty
    private String fieldValue;
    @JsonIgnore
    private ConfigurationTemplate configurationTemplate;
    private ConfigurationType configurationType;
    @JsonIgnore
    @Valid
    private Configuration configuration;
    private String fieldCommentary;
    private IndexType indexType;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

 
    public ConfigurationTemplate getConfigurationTemplate() {
		return configurationTemplate;
	}

	public void setConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
		this.configurationTemplate = configurationTemplate;
	}

	public String getFieldCommentary() {
        return fieldCommentary;
    }

    public void setFieldCommentary(String fieldCommentary) {
        this.fieldCommentary = fieldCommentary;
    }

    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(ConfigurationType configurationType) {
        this.configurationType = configurationType;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }
}
