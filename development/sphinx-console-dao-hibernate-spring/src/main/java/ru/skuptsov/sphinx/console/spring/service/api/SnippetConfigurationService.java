package ru.skuptsov.sphinx.console.spring.service.api;

import java.util.List;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.params.SnippetSearchParameters;

public interface SnippetConfigurationService extends Service<SnippetConfiguration> {
	List<SnippetConfiguration> getSnippetConfigurations(SnippetSearchParameters parameters);
	Long count(SnippetSearchParameters parameters);
	List<SnippetConfiguration> getSnippets();
    SnippetConfiguration getSnippet(String collectionName);
}
