package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.CreateReplicaTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.CreateReplicaTaskService;

@Service("createReplicaTaskService")
public class CreateReplicaTaskServiceImpl extends AbstractTaskService<CreateReplicaTask> implements CreateReplicaTaskService {

}
