package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.CollectionRoleType;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationTemplate;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationType;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface ConfigurationTemplateDao extends Dao<ConfigurationTemplate> {

	List<ConfigurationTemplate> getConfigurationTemplates();
    List<ConfigurationTemplate> getSearchConfigurationTemplates();
    List<ConfigurationTemplate> getIndexConfigurationTemplates();
    void addConfigurationTemplate(ConfigurationTemplate configurationTemplate);
    void updateConfigurationTemplate(ConfigurationTemplate configurationTemplate);
    void deleteConfigurationTemplate(Long id);
    ConfigurationTemplate getConfigurationTemplate(String name);
    ConfigurationTemplate getConfigurationTemplate(String name, ConfigurationType configurationType, CollectionRoleType collectionType);
}
