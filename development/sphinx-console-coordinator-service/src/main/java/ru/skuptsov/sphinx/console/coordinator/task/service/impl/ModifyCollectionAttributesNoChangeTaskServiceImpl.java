package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesNoChangeTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ModifyCollectionAttributesNoChangeTaskService;

@Service("modifyCollectionAttributesNoChangeTaskService")
public class ModifyCollectionAttributesNoChangeTaskServiceImpl extends AbstractTaskService<ModifyCollectionAttributesNoChangeTask> implements ModifyCollectionAttributesNoChangeTaskService {
}
