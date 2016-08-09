package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.RebuildSnippetsTaskService;


@Service("rebuildSnippetsTaskService")
public class RebuildSnippetsTaskServiceImpl extends AbstractTaskService<RebuildSnippetsTask> implements RebuildSnippetsTaskService {

}
