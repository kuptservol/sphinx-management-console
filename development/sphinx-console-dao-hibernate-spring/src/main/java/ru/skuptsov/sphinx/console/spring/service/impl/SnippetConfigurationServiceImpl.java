package ru.skuptsov.sphinx.console.spring.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.params.SnippetSearchParameters;
import ru.skuptsov.sphinx.console.dao.api.SnippetConfigurationDao;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;

@Service
public class SnippetConfigurationServiceImpl extends AbstractSpringService<SnippetConfigurationDao, SnippetConfiguration> implements SnippetConfigurationService {

	@Override
	@Transactional(readOnly = true)
	public List<SnippetConfiguration> getSnippetConfigurations(SnippetSearchParameters parameters) {
		return getDao().find(parameters);
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(SnippetSearchParameters parameters) {
		return getDao().count(parameters);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SnippetConfiguration> getSnippets() {
		return getDao().getSnippets();
	}

    @Override
    @Transactional(readOnly = true)
    public SnippetConfiguration getSnippet(String collectionName) {
        SnippetConfiguration snippet = null;
        SnippetSearchParameters parameters = new SnippetSearchParameters();
        parameters.setCollectionName(collectionName);

        List<SnippetConfiguration> snippets = getDao().find(parameters);
        if(snippets != null && snippets.size() > 0) {
            snippet = snippets.get(0);
        }

        return snippet;
    }
}
