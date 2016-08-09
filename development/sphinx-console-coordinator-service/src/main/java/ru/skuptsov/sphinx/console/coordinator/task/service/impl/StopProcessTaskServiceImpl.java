package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.StopProcessTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.StopProcessTaskService;

@Service("stopProcessTaskService")
public class StopProcessTaskServiceImpl extends AbstractTaskService<StopProcessTask> implements StopProcessTaskService {

}
