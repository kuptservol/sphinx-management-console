package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.MakeCollectionFullRebuildIndexTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.MakeCollectionFullRebuildIndexTaskService;

@Service("MakeCollectionFullRebuildIndexTaskService")
public class MakeCollectionFullRebuildIndexTaskServiceImpl extends AbstractTaskService<MakeCollectionFullRebuildIndexTask> implements MakeCollectionFullRebuildIndexTaskService {

}
