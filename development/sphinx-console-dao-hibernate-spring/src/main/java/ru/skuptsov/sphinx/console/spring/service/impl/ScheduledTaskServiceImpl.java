package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.dao.api.ScheduledTaskDao;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;

import java.util.List;

@Service
public class ScheduledTaskServiceImpl extends AbstractSpringService<ScheduledTaskDao, ScheduledTask> implements ScheduledTaskService {

	@Override
	@Transactional(readOnly = true)
	public List<ScheduledTask> getScheduledTasks() {
		return getDao().getScheduledTasks();
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<ScheduledTask> getScheduledTasks(ScheduledTaskType type) {
		return getDao().getScheduledTasks(type);
	}

    @Override
    @Transactional
    public ScheduledTask findByCollectionName(String collectionName, ScheduledTaskType type) {
        return getDao().getByCollectionName(collectionName, type);
    }
}
