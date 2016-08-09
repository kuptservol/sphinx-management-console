package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.ModifyDistributedReplicaTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ModifyDistributedReplicaPortTaskService;

@Service("modifyDistributedReplicaPortTaskService")
public class ModifyDistributedReplicaPortTaskServiceImpl extends AbstractTaskService<ModifyDistributedReplicaTask> implements ModifyDistributedReplicaPortTaskService {

}
