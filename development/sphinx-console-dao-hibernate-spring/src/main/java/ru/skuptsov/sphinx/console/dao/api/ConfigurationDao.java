package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface ConfigurationDao extends Dao<Configuration> {
    Configuration getConfiguration(String name);
    void clearFieldMappings(Long configurationId);
    void clearSourceConfigurationFields(Long configurationId);
    void clearConfigurationFields(Long configurationTemplateId);
    List<Configuration> getConfigurationByConfigurationTemplate(Long configurationTemplateId);
    List<Long> getConfigurationsIds(String collectionName);
}
