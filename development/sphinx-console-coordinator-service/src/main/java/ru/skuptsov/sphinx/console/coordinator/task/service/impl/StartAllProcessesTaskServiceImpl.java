package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.StartAllProcessesTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.StartAllProcessesTaskService;

@Service("startAllProcessesTaskService")
public class StartAllProcessesTaskServiceImpl extends AbstractTaskService<StartAllProcessesTask> implements StartAllProcessesTaskService  {

}
