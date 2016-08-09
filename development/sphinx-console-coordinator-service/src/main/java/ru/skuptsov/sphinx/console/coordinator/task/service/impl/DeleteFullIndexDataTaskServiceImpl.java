package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.task.DeleteFullIndexDataTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.DeleteFullIndexDataTaskService;


@Service("deleteFullIndexDataTaskService")
public class DeleteFullIndexDataTaskServiceImpl extends AbstractTaskService<DeleteFullIndexDataTask> implements DeleteFullIndexDataTaskService {

}
