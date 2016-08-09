package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.RebuildCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.RebuildCollectionTaskService;

@Service("rebuildCollectionTaskService")
public class RebuildCollectionTaskServiceImpl extends AbstractTaskService<RebuildCollectionTask> implements RebuildCollectionTaskService {

}
