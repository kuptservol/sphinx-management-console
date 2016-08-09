package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.ModifyDistributedCollectionAttributesRestoreFailureTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ModifyDistributedCollectionAttributesRestoreFailureTaskService;

@Service("modifyDistributedCollectionAttributesRestoreFailureTaskService")
public class ModifyDistributedCollectionAttributesRestoreFailureTaskServiceImpl extends AbstractTaskService<ModifyDistributedCollectionAttributesRestoreFailureTask> implements
    ModifyDistributedCollectionAttributesRestoreFailureTaskService {
}
