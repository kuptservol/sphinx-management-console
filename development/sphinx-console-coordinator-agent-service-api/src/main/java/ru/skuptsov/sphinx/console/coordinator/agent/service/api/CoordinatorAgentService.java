package ru.skuptsov.sphinx.console.coordinator.agent.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public interface CoordinatorAgentService {
    String test();
    
    Status setCoordinator(Task task);
    Status startRotating(Task task, String processName);
    Status startCreatingProcess(Task task, String processName, String sphinxServiceContent);
    Status addProcessToStartup(Task task, String processName);
    Status removeProcessFromStartup(Task task, String processName);
    Status startIndexing(Task task, String processName);
    Status startUpdatingConfig(Task task, String processName, String content);
    Status startPushingFiles(Task task, String processName);
    Status startDeletingProcess(Task task, String processName);
    Status stopProcess(Task task, String processName);
    Status startProcess(Task task, String processName);
    Status startDeletingIndexData(Task task, String processName);
    Status startCleaningIndexDataFolder(Task task, String processName);
    Status moveFiles(Task task, String processName, String fromServer, String toServer);
    Status stopIndexing(Task task, String processName);
    Status stopProcessIgnoreFailure(Task task, String processName);
    Status startMerging(Task task);
    Status startDeleteIndexDataFiles(Task task, String processName);
    Status startDeletingSnippetData(Task task, String processName);
    Status runSnippetQuery(Task task, SnippetConfiguration configuration);
    Status runRSyncCommand(Task task, String processName);
    Status stopSnippetQuery(Task task);
}
