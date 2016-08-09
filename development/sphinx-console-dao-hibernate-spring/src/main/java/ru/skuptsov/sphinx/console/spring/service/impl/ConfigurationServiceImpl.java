package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.dao.api.ConfigurationDao;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;

import java.util.List;

@Service
public class ConfigurationServiceImpl extends AbstractSpringService<ConfigurationDao, Configuration> implements ConfigurationService {

    @Override
    @Transactional(readOnly = true)
    public Configuration getConfiguration(String name) {
        return getDao().getConfiguration(name);
    }

	@Override
	@Transactional
	public void merge(Configuration configuration) {
		getDao().merge(configuration);
	}

	@Override
	@Transactional
	public void clearFieldMappings(Long configurationId) {
		getDao().clearFieldMappings(configurationId);
	}

	@Override
	@Transactional
	public void clearConfigurationFields(Long configurationTemplateId) {
		getDao().clearConfigurationFields(configurationTemplateId);
	}

    @Override
    @Transactional
    public List<Configuration> getConfigurationByConfigurationTemplate(Long configurationTemplateId) {
        return getDao().getConfigurationByConfigurationTemplate(configurationTemplateId);
    }

	@Override
	@Transactional
	public void clearSourceConfigurationFields(Long configurationId) {
		getDao().clearSourceConfigurationFields(configurationId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> getConfigurationsIds(String collectionName) {
		return getDao().getConfigurationsIds(collectionName);
	}
}
