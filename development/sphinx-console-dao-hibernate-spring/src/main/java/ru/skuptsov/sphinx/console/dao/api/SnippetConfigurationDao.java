package ru.skuptsov.sphinx.console.dao.api;

import java.util.List;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.params.SnippetSearchParameters;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

public interface SnippetConfigurationDao extends Dao<SnippetConfiguration> {
	List<SnippetConfiguration> find(SnippetSearchParameters parameters);
	Long count(SnippetSearchParameters parameters);
	List<SnippetConfiguration> getSnippets();
}
