package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.DeleteSnippetConfigurationTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.DeleteSnippetConfigurationTaskService;

@Service("deleteSnippetConfigurationTaskService")
public class DeleteSnippetConfigurationTaskServiceImpl extends AbstractTaskService<DeleteSnippetConfigurationTask> implements DeleteSnippetConfigurationTaskService {

}
