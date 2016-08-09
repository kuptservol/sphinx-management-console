package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.DataSource;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface DataSourceDao extends Dao<DataSource> {
	List<DataSource> getDataSources();
    DataSource findDataSource(String name);
}
