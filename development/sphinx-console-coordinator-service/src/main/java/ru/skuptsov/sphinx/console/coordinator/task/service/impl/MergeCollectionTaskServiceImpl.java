package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.MergeCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.MergeCollectionTaskService;

@Service("mergeCollectionTaskService")
public class MergeCollectionTaskServiceImpl extends AbstractTaskService<MergeCollectionTask> implements MergeCollectionTaskService {

}
