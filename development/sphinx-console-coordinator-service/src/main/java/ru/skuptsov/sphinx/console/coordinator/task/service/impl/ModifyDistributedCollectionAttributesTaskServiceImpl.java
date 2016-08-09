package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.ModifyDistributedCollectionAttributesTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ModifyDistributedCollectionAttributesTaskService;

@Service("modifyDistributedCollectionAttributesTaskService")
public class ModifyDistributedCollectionAttributesTaskServiceImpl extends AbstractTaskService<ModifyDistributedCollectionAttributesTask> implements ModifyDistributedCollectionAttributesTaskService {

}
