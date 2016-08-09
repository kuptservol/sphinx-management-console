package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.dao.api.ConfigurationFieldsDao;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;

@Service
public class ConfigurationFieldsServiceImpl extends AbstractSpringService<ConfigurationFieldsDao, ConfigurationFields> implements ConfigurationFieldsService {

	@Override
	@Transactional
	public void merge(ConfigurationFields configurationField) {
		getDao().merge(configurationField);
	}

    @Override
    @Transactional
    public ConfigurationFields getSearchPort(Long configurationId) {
        return getDao().getSearchPort(configurationId);
    }
    
    @Override
    @Transactional
    public ConfigurationFields getDistributedPort(Long configurationId) {
        return getDao().getDistributedPort(configurationId);
    }

    
}
