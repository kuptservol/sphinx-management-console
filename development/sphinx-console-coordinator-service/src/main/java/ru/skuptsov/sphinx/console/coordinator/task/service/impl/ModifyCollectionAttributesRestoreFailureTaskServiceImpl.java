package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesRestoreFailureTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ModifyCollectionAttributesRestoreFailureTaskService;

@Service("modifyCollectionAttributesRestoreFailureTaskService")
public class ModifyCollectionAttributesRestoreFailureTaskServiceImpl extends AbstractTaskService<ModifyCollectionAttributesRestoreFailureTask> implements ModifyCollectionAttributesRestoreFailureTaskService {
}
