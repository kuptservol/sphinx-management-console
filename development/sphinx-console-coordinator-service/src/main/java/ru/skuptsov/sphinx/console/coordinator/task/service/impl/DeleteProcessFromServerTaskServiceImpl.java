package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteProcessFromServerTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.DeleteProcessFromServerTaskService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 15.08.14
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteProcessFromServerTaskService")
public class DeleteProcessFromServerTaskServiceImpl extends AbstractTaskService<DeleteProcessFromServerTask> implements DeleteProcessFromServerTaskService {
}
