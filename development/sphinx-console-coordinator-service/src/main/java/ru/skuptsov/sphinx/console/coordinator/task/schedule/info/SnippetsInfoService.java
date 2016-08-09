package ru.skuptsov.sphinx.console.coordinator.task.schedule.info;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetInfoWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.FullRebuildSnippetTask;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;

@Component
public class SnippetsInfoService {
	private static final Logger logger = LoggerFactory.getLogger(SnippetsInfoService.class);
	
	@Autowired
    private CollectionService collectionService;
	
	@Autowired
    private SnippetConfigurationService snippetConfigurationService;
	
	@Autowired
    private ScheduledTaskService scheduledTaskService;
	
	@Resource
    protected ConcurrentHashMap<String, SnippetInfoWrapper> snippetsInfoMap;
	
    @Autowired
    protected TasksMapService tasksMapService;
	
	@Scheduled(fixedDelayString = "${query.snippets.info.delay}")
    public void process() {
        logger.info("ABOUT TO PROCESS SNIPPETS INFO...");
        
        ConcurrentHashMap<String, SnippetInfoWrapper> map = new ConcurrentHashMap<String, SnippetInfoWrapper>();
        
        List<SnippetConfiguration> snippets = snippetConfigurationService.getSnippets();
        
        for (SnippetConfiguration snippet : snippets) {
            Collection collection = snippet.getCollection();
            String collectionName = collection.getName();
            SnippetInfoWrapper info = new SnippetInfoWrapper();
            info.setIsCurrentlyRebuildSnippet(isCurrentlyRebuildSnippet(collectionName));
            info.setIsCurrentlyFullRebuildSnippet(isCurrentlyFullRebuildSnippet(collectionName));
            
            ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(collectionName, ScheduledTaskType.BUILD_SNIPPET);
            if(scheduledTask != null) {
            	info.setIsSheduleEnabled(scheduledTask.getIsEnabled());
            }
            
            map.put(collectionName, info);
        }
        
        snippetsInfoMap.clear();
        snippetsInfoMap.putAll(map);
	}
	
	private boolean isCurrentlyRebuildSnippet(String collectionName) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(tasksMapService.getAllTasks());
		
		for (Task task : tasks) {
		    if (task instanceof RebuildSnippetsTask && task.getCollectionName() != null && task.getCollectionName().equals(collectionName)) {
		        return task.getTaskStatus() == TaskStatus.RUNNING;	
		    }
		}
		
		return false;
		
	}
	
	private boolean isCurrentlyFullRebuildSnippet(String collectionName) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(tasksMapService.getAllTasks());
		
		for (Task task : tasks) {
		    if (task instanceof FullRebuildSnippetTask && task.getCollectionName() != null && task.getCollectionName().equals(collectionName)) {
		        return task.getTaskStatus() == TaskStatus.RUNNING;	
		    }
		}
		
		return false;
		
	}
}
