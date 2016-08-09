package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.RemoveReplicaTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.RemoveReplicaTaskService;

@Service("removeReplicaTaskService")
public class RemoveReplicaTaskServiceImpl extends AbstractTaskService<RemoveReplicaTask> implements RemoveReplicaTaskService {

}
