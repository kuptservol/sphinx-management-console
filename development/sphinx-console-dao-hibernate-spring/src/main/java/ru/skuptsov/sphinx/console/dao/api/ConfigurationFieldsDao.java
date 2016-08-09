package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

public interface ConfigurationFieldsDao extends Dao<ConfigurationFields> {

    ConfigurationFields getSearchPort(Long configurationId);
    
    ConfigurationFields getDistributedPort(Long configurationId);

}
