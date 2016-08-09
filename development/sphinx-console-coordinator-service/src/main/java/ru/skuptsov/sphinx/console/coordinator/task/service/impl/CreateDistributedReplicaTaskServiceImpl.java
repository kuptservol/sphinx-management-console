package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.CreateDistributedReplicaTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.CreateDistributedReplicaTaskService;

@Service("createDistributedReplicaTaskService")
public class CreateDistributedReplicaTaskServiceImpl extends AbstractTaskService<CreateDistributedReplicaTask> implements CreateDistributedReplicaTaskService {

}
