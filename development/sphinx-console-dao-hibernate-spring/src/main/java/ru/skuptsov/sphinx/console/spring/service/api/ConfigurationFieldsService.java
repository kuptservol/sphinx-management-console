package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;

public interface ConfigurationFieldsService extends Service<ConfigurationFields> {

    void merge(ConfigurationFields configurationField);

    //возвращает настройку listen port для searchd
    ConfigurationFields getSearchPort(Long configurationId);
    
    ConfigurationFields getDistributedPort(Long configurationId);
}
