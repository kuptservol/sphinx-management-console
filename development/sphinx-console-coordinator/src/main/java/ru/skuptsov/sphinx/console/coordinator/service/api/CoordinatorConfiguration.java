package ru.skuptsov.sphinx.console.coordinator.service.api;


import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;

public interface CoordinatorConfiguration {
    Status addCollection(CollectionWrapper collectionWrapper, BindingResult result);
    Status makeCollectionFullRebuildIndex(String collectionName, String taskType);
    Status makeCollectionFullRebuildApply(String collectionName, String taskType);
    Status deleteCollection(String collectionName);
    Status addServer(Server server, BindingResult result);
    Status deleteServer(Long serverId);
    Status deleteServer(String serverName);
    Status addAdminProcess(AdminProcess adminProcess, BindingResult result);
    Status addAdminProcesses(ServerWrapper serverWrapper, BindingResult result);
    Status updateAdminProcess(AdminProcess adminProcess, BindingResult result);
    Status deleteAdminProcess(Long adminProcessId);
    Status rebuildCollection(String collectionName);
    Status mergeCollection(String collectionName);
    Status stopMerging(String collectionName);
    Status modifyCollectionAttributes(CollectionWrapper collectionWrapper, BindingResult result);
    Status changeCollectionUpdateSchedule(UpdateScheduleWrapper updateScheduleWrapper, BindingResult result);
    Status moveProcessToServer(MoveProcessToServerWrapper moveProcessToServerWrapper, BindingResult result);
    Status stopProcess(String collectionName, Long replicaNumber);
    Status stopAllProcesses(String collectionName);
    Status startProcess(String collectionName, Long replicaNumber);
    Status startAllProcesses(String collectionName);
    Status stopTask(TaskWrapper task, BindingResult result);
    Status pauseTask(TaskWrapper task, BindingResult result);
    Status resumeTask(TaskWrapper task, BindingResult result);
    Status checkDBConnection(DataSource datasource, BindingResult result);
    Status stopIndexing(String collectionName);
    Status stopFullRebuildIndexing(String collectionName, String serverName);
    ResponseWrapper<Boolean> validateCronExpression(RequestWrapper<String> cronExpression);
    Status addConfigurationTemplate(ConfigurationTemplate configurationTemplate, BindingResult bindingResult);
    Status updateConfigurationTemplate(Long id, ConfigurationTemplate configurationTemplate, BindingResult result);
    Status deleteConfigurationTemplate(Long id);
    Status disableScheduling(String collectionName, String taskType);
    Status enableScheduling(String collectionName, String taskType);
    Status createReplica(ReplicaWrapper replicaWrapper, BindingResult result);
    Status removeReplica(ReplicaWrapper replicaWrapper, BindingResult result);
    Status modifyReplicaPort(ReplicaWrapper replicaWrapper, BindingResult result);
    Status deleteFullIndexData(String collectionName);
    Status saveDataSource(DataSource dataSource);
    Status addDistributedCollection(DistributedCollectionWrapper distributedCollectionWrapper);
    Status reloadDistributedCollection(String collectionName);
    Status createDistributedReplica(ReplicaWrapper replicaWrapper, BindingResult result);
    Status modifyDistributedReplicaPort(ReplicaWrapper replicaWrapper, BindingResult result);
    Status modifyDistributedCollectionAttributes(DistributedCollectionWrapper collectionWrapper, BindingResult result);
    Status createSnippetConfiguration(SnippetConfigurationWrapper snippetConfigurationWrapper, BindingResult result);
    Status deleteSnippetConfiguration(String collectionName);
    Status rebuildSnippets(String collectionName);
    Status makeSnippetsFullRebuild(String collectionName);
    Status editSnippetConfiguration(SnippetConfigurationWrapper snippetConfigurationWrapper, BindingResult result);
    Status stopRebuildSnippets(String collectionName);
    Status stopFullRebuildSnippets(String collectionName);

    @RequestMapping(value = CoordinatorConfigurationRestURIConstants.DELETE_ALL_SEARCH_QUERIES, method = RequestMethod.GET)
    void deleteAllSearchQueries();
}
