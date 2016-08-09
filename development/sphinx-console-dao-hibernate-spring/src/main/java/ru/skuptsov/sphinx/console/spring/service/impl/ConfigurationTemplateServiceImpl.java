package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.skuptsov.sphinx.console.coordinator.model.CollectionRoleType;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationTemplate;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationType;
import ru.skuptsov.sphinx.console.dao.api.ConfigurationTemplateDao;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationTemplateService;

import java.util.List;

@Service
public class ConfigurationTemplateServiceImpl extends AbstractSpringService<ConfigurationTemplateDao, ConfigurationTemplate> implements ConfigurationTemplateService {

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationTemplate> getIndexConfigurationTemplates() {
        return getDao().getIndexConfigurationTemplates();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationTemplate> getSearchConfigurationTemplates() {
        return getDao().getSearchConfigurationTemplates();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationTemplate> getConfigurationTemplates() {
        return getDao().getConfigurationTemplates();
    }

    @Override
    @Transactional
    public void addConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        getDao().addConfigurationTemplate(configurationTemplate);
    }

    @Override
    @Transactional
    public void updateConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        getDao().updateConfigurationTemplate(configurationTemplate);
    }

    @Override
    @Transactional
    public void deleteConfigurationTemplate(Long id) {
        getDao().deleteConfigurationTemplate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationTemplate getConfigurationTemplate(Long id) {
        return getDao().findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationTemplate getConfigurationTemplate(String name) {
        return getDao().getConfigurationTemplate(name);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationTemplate getConfigurationTemplate(String name, ConfigurationType configurationType, CollectionRoleType collectionType) {
        return getDao().getConfigurationTemplate(name, configurationType, collectionType);
    }

}
