package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.StartProcessTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.StartProcessTaskService;

@Service("startProcessTaskService")
public class StartProcessTaskServiceImpl extends AbstractTaskService<StartProcessTask> implements StartProcessTaskService {

}
