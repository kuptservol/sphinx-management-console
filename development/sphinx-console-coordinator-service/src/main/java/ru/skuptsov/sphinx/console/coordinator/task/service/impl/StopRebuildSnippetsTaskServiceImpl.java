package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.StopRebuildSnippetsTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.StopRebuildSnippetsTaskService;

@Service("stopRebuildSnippetsTaskService")
public class StopRebuildSnippetsTaskServiceImpl extends AbstractTaskService<StopRebuildSnippetsTask> implements StopRebuildSnippetsTaskService {

}
