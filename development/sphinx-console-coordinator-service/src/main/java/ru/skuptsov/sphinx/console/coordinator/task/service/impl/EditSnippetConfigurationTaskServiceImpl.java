package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.EditSnippetConfigurationTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.EditSnippetConfigurationTaskService;

@Service("editSnippetConfigurationTaskService")
public class EditSnippetConfigurationTaskServiceImpl extends AbstractTaskService<EditSnippetConfigurationTask> implements EditSnippetConfigurationTaskService {

}
