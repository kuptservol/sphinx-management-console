package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.DeleteCollectionTaskService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteCollectionTaskService")
public class DeleteCollectionTaskServiceImpl extends AbstractTaskService<DeleteCollectionTask> implements DeleteCollectionTaskService {
}
