package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.StopIndexingTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.StopIndexingTaskService;


@Service("stopIndexingTaskService")
public class StopIndexingTaskServiceImpl extends AbstractTaskService<StopIndexingTask> implements StopIndexingTaskService {

}
