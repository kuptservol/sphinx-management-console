package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.CreateSnippetConfigurationTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.CreateSnippetConfigurationTaskService;

@Service("createSnippetConfigurationTaskService")
public class CreateSnippetConfigurationTaskServiceImpl extends AbstractTaskService<CreateSnippetConfigurationTask> implements CreateSnippetConfigurationTaskService {

}
