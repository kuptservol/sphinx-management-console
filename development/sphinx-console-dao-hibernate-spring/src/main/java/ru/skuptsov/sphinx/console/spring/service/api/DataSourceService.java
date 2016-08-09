package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.DataSource;

import java.util.List;

public interface DataSourceService extends Service<DataSource> {
    List<DataSource> getDataSources();
    DataSource getDataSource(String name);
}
