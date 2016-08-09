package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.CollectionRoleType;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationTemplate;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationType;

import java.util.List;

public interface ConfigurationTemplateService {
    List<ConfigurationTemplate> getConfigurationTemplates();
    List<ConfigurationTemplate> getSearchConfigurationTemplates();
    List<ConfigurationTemplate> getIndexConfigurationTemplates();
    void addConfigurationTemplate(ConfigurationTemplate configurationTemplate);
    void updateConfigurationTemplate(ConfigurationTemplate configurationTemplate);
    void deleteConfigurationTemplate(Long id);
    ConfigurationTemplate getConfigurationTemplate(Long id);
    ConfigurationTemplate getConfigurationTemplate(String name);
    ConfigurationTemplate getConfigurationTemplate(String name, ConfigurationType configurationType, CollectionRoleType collectionType);
}
