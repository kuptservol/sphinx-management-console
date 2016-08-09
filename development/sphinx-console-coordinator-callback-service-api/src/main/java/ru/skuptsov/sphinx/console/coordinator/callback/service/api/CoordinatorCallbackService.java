package ru.skuptsov.sphinx.console.coordinator.callback.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public interface CoordinatorCallbackService {
    String test();

    Status processIsDown();

    Status creatingProcessFinished(Status status, Task task);

    Status addProcessToStartupCommandFinished(Status status, Task task);

    Status removeProcessFromStartupCommandFinished(Status status, Task task);

    Status indexingFinished(Status status, Task task);

    Status rotatingFinished(Status status, Task task);

    Status updatingConfigFinished(Status status, Task task);

    Status pushingFilesFinished(Status status, Task task);

    Status deletingProcessFinished(Status status, Task task);

    Status deletingIndexDataFinished(Status status, Task task);

    Status startProcessFinished(Status status, Task task);

    Status stopProcessFinished(Status status, Task task);

    Status moveFilesFinished(Status status, Task task);

    Status stopIndexingFinished(Status status, Task task);

    Status startMergingFinished(Status status, Task task);

    Status deleteIndexDataFilesFinished(Status status, Task task);

    Status deletingSnippetDataFinished(Status status, Task task);

    Status runSnippetQueryFinished(Status status, Task task);

    Status runRSyncCommandFinished(Status status, Task task);

    Status stopSnippetQueryFinished(Status status, Task task);
}
