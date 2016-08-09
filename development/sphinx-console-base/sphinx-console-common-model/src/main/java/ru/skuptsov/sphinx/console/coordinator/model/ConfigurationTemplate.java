package ru.skuptsov.sphinx.console.coordinator.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.constraints.NotNull;

public class ConfigurationTemplate extends BaseEntity {
	private Long id;
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    private Boolean defaultTemplate;
    @NotNull
    private Boolean systemTemplate;
    @NotNull
    private ConfigurationType type;
    
    @JsonDeserialize(as=LinkedHashSet.class)
    private Set<ConfigurationFields> configurationFields = new LinkedHashSet<ConfigurationFields>();
    
    private CollectionRoleType collectionType = CollectionRoleType.SIMPLE;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public Boolean getSystemTemplate() {
        return systemTemplate;
    }

    public void setSystemTemplate(Boolean system) {
        this.systemTemplate = system;
    }

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

	public Set<ConfigurationFields> getConfigurationFields() {
		if (configurationFields != null) {
			return new LinkedHashSet<ConfigurationFields>(configurationFields);	
		}
		return configurationFields;
	}

	@JsonDeserialize(as=LinkedHashSet.class)
	public void setConfigurationFields(Set<ConfigurationFields> configurationFields) {
		if (configurationFields != null) {
		   this.configurationFields = new LinkedHashSet<ConfigurationFields>(configurationFields);
		} else {
		   this.configurationFields = configurationFields;
		}
	}

	public CollectionRoleType getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(CollectionRoleType collectionType) {
		this.collectionType = collectionType;
	}
    
    
}
