package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyReplicaTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ModifyReplicaPortTaskService;

/**
 * Created by Andrey on 29.01.2015.
 */
@Service("modifyReplicaPortTaskService")
public class ModifyReplicaPortTaskServiceImpl extends AbstractTaskService<ModifyReplicaTask> implements ModifyReplicaPortTaskService {
}
