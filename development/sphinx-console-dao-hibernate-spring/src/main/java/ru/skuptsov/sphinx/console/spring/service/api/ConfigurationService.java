package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;

import java.util.List;

public interface ConfigurationService extends Service<Configuration> {
    Configuration getConfiguration(String name);
    void merge(Configuration configuration);
    void clearFieldMappings(Long configurationId);
    void clearSourceConfigurationFields(Long configurationId);
    
    void clearConfigurationFields(Long configurationTemplateId);
    List<Configuration> getConfigurationByConfigurationTemplate(Long configurationTemplateId);
    List<Long> getConfigurationsIds(String collectionName);
}
