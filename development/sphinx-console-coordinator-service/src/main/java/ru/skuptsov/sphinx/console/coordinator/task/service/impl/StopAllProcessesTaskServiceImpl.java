package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.StopAllProcessesTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.StopAllProcessesTaskService;

@Service("stopAllProcessesTaskService")
public class StopAllProcessesTaskServiceImpl extends AbstractTaskService<StopAllProcessesTask> implements StopAllProcessesTaskService {

}
