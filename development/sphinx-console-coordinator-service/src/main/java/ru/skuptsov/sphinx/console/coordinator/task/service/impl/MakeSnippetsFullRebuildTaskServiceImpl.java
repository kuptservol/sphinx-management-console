package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.FullRebuildSnippetTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.MakeSnippetsFullRebuildTaskService;

/**
 * Created by Developer on 06.07.2015.
 */
@Service("makeSnippetsFullRebuildTaskService")
public class MakeSnippetsFullRebuildTaskServiceImpl extends AbstractTaskService<FullRebuildSnippetTask> implements MakeSnippetsFullRebuildTaskService {

}