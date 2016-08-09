package ru.skuptsov.sphinx.console.coordinator.task.schedule.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteFullIndexDataTask;
import ru.skuptsov.sphinx.console.coordinator.task.MakeCollectionFullRebuildApplyTask;
import ru.skuptsov.sphinx.console.coordinator.task.MakeCollectionFullRebuildIndexTask;
import ru.skuptsov.sphinx.console.spring.service.api.*;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

@Component
public class CollectionsInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CollectionsInfoService.class);

    private static final String INDEXING_OPERATION = "START_INDEXING_INDEX"; //TODO
    private static final String COPY_FULL_INDEX_OPERATION = "COPY_FULL_INDEX_STUB"; //TODO
    private static final String STOP_INDEXING_OPERATION = "STOP_INDEXING"; //TODO

    public static final Long FIRST_REPLICA = 1L;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    protected ServerService serverService;

    @Autowired
    protected ProcessService processService;

    @Autowired
    protected ActivityLogService activityLogService;

    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Value("${agent.info.parallel}")
    private String agentInfoParallel="false";


    @Resource
    protected ConcurrentHashMap<String, CollectionInfoWrapper> collectionsInfoMap;

    @Scheduled(fixedDelayString = "${query.collections.info.delay}")
    public void process() {

        logger.debug("agent.info.parallel " + agentInfoParallel);
        updateCollectionInfo(Boolean.valueOf(agentInfoParallel));
    }

    public void updateCollectionInfo(boolean isParallel) {
        logger.info("ABOUT TO PROCESS CACHE OF COLLECTIONS...");

        ConcurrentHashMap<String, CollectionInfoWrapper> map = new ConcurrentHashMap<String, CollectionInfoWrapper>();

        List<Collection> collections = collectionService.getCollections();

        /* //REMOVE DELETED COLLECTIONS FROM MAP
        Set<String> deletedKeys = collectionsInfoMap.keySet();
        Set<String> currentKeys = new HashSet<String>();
        for(Collection collection : collections) {
            currentKeys.add(collection.getName());
        }
        deletedKeys.removeAll(currentKeys);
        for(String key : deletedKeys) {
            collectionsInfoMap.remove(key);
        }*/


        if(!isParallel)
        {
            // Пройтись по всем коллекциям последовательно
            for (Collection collection : collections) {
                updateCollectionInfoForCollection(map, collection);
            }
        }
        else
        {
            // Пройтись по всем коллекциям параллельно
            if(collections.size() > 0){
                int poolSize = collections.size();
                ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
                List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

                for (Collection collection : collections) {
                    final Collection collectionF = collection;
                    final Map<String, CollectionInfoWrapper> mapF = map;

                    Future f = executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            updateCollectionInfoForCollection(mapF, collectionF);
                        }
                    });
                    futures.add(f);
                }

                for (Future<Runnable> f : futures)
                {
                    try {
                        f.get();
                    } catch (InterruptedException e) {
                        logger.error("Error updating collection info ", e);
                    } catch (ExecutionException e) {
                        logger.error("Error updating collection info ", e);
                    } catch (Exception e) {
                        logger.error("Error updating collection info ", e);
                    }
                }

                executorService.shutdownNow();
            }
        }

        collectionsInfoMap.clear();
        collectionsInfoMap.putAll(map);
        logger.info("PROCESS CACHE OF COLLECTIONS FINISHED");
    }

    private void updateCollectionInfoForCollection(final Map<String, CollectionInfoWrapper> map, final Collection collection)
    {

        map.put(collection.getName(), new CollectionInfoWrapper());
        try {
            map.get(collection.getName()).setCollectionSize(getCollectionSize(collection.getName()));
        } catch (Throwable e) {
            logger.error("Error getting collection size", e);
        }
        try {
            map.get(collection.getName()).setProcessStatuses(getProcessStatuses(collection));
        } catch (Throwable e) {
            logger.error("Error getting agent Status", e);
        }
        try {
            map.get(collection.getName()).setIsCurrentlyIndexing(isCurrentlyIndexing(collection.getName()));
        } catch (Throwable e) {
            logger.error("Error getting process Status", e);
        }
        try {
            map.get(collection.getName()).setIsCurrentlyIndexingDelta(isCurrentlyIndexingDelta(collection.getName()));
        } catch (Throwable e) {
            logger.error("Error getting process Status", e);
        }
        try {
            map.get(collection.getName()).setIsCurrentlyMerging(isCurrentlyMerging(collection.getName()));
        } catch (Throwable e) {
            logger.error("Error getting process Status", e);
        }
        try {
            map.get(collection.getName()).setFullIndexingResult(getFullIndexingResult(collection.getName()));
        } catch (Throwable e) {
            logger.error("Error getting process Status", e);
        }
        try {
            map.get(collection.getName()).setTaskUid(getFullRebuildApplyTaskUid(collection.getName()));
        } catch (Throwable e) {
            logger.error("Error getting process Status", e);
        }
        try {
            map.get(collection.getName()).setNextIndexingTime(collection.getNextIndexingTime());
            map.get(collection.getName()).setLastIndexingTime(collection.getLastIndexingTime());
            map.get(collection.getName()).setNextMergeTime(collection.getNextMergeTime());
            map.get(collection.getName()).setLastMergeTime(collection.getLastMergeTime());
            map.get(collection.getName()).setProcessingFailed(collection.getProcessingFailed());
        } catch (Throwable e) {
            logger.error("Error getting process Status", e);
        }
    }

    private Long getCollectionSize(String collectionName) {
        Long result;
        try {
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, FIRST_REPLICA);
            if (searchSphinxProcess == null) return 0L;
            Server server = searchSphinxProcess.getServer();
            AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
            MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
            Integer port = Integer.valueOf(searchSphinxProcess.getConfiguration().getSearchListenPort());
            result = monitoringService.getCollectionSize(server.getIp(), adminProcess.getPort(), port, collectionName);
        } catch(Throwable e) {
            logger.error("ERROR OCCURED WHILE RETRIVING COLLECTION SIZE:", e);
            result = 0L;
        }
        return result != null ? result : 0L;
    }

    private Map<Long,ProcessStatus> getProcessStatuses(Collection collection) {
        Map<Long,ProcessStatus> result = new HashMap<Long, ProcessStatus>();
        for(Replica replica : collection.getReplicas()) {
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(collection.getName(), replica.getNumber());
            if (searchSphinxProcess == null) continue;
            Server server = searchSphinxProcess.getServer();
            AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
            MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
            String processName = collection.getName() + "_" + replica.getNumber();
            result.put(replica.getNumber(), monitoringService.isProcessAlive(server.getIp(), adminProcess.getPort(), processName));
        }
        return result;
    }

    public Boolean isCurrentlyIndexing(String collectionName) {
        return isCurrentlyIndexingMerging(collectionName, null, false);
    }

    public Boolean isCurrentlyIndexingDelta(String collectionName) {
        return isCurrentlyIndexingMerging(collectionName, collectionName + "_" + IndexType.DELTA.getTitle(), false);
    }

    public Boolean isCurrentlyMerging(String collectionName) {
        return isCurrentlyIndexingMerging(collectionName, null, true);
    }

    private Boolean isCurrentlyIndexingMerging(String collectionName, String indexName, boolean checkMerging) {
        List<SphinxProcess> indexSphinxProcesses = processService.findByCollectionNameAndType(collectionName, SphinxProcessType.INDEXING);
        SphinxProcess indexSphinxProcess = null;
        if (indexSphinxProcesses != null && !indexSphinxProcesses.isEmpty()) {
            indexSphinxProcess = indexSphinxProcesses.get(0);
        }

        if (indexSphinxProcess == null) {
            return false;
        }

        Server server = indexSphinxProcess.getServer();
        String ip = server.getIp();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, server);
        Integer port = adminProcess.getPort();

        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

        Boolean result = false;
        try {
            if(checkMerging){
                result = monitoringService.isCurrentlyMerging(ip, port, collectionName + "_" + FIRST_REPLICA);
            }else{
                if(StringUtils.isEmpty(indexName)){
                    result = monitoringService.isCurrentlyIndexing(ip, port, collectionName + "_" + FIRST_REPLICA);
                }
                else {
                    result = monitoringService.isCurrentlyIndexing(ip, port, collectionName + "_" + FIRST_REPLICA, indexName);
                }
            }
        } catch(Exception e) {
            logger.error("ERROR OCCURED WHILE CHECKING COLLECTION IS CURRENTLY INDEXING/MERGING:", e);
            result = false;
        }

        return result;
    }

    private String getFullRebuildApplyTaskUid(String collectionName){
        ActivityLog indexLastLog = activityLogService.getLast(collectionName, MakeCollectionFullRebuildApplyTask.TASK_NAME, null);
        return indexLastLog != null ? indexLastLog.getTaskUid() : null;
    }

    public FullIndexingResult getFullIndexingResult(String collectionName){

        FullIndexingResult result = new FullIndexingResult();
        result.setFullIndexingState(FullIndexingState.NOT_RUNNING);

        SphinxProcess fullIndexingSphinxProcess = processService.findFullIndexingProcess(collectionName);
        if(fullIndexingSphinxProcess != null) {
            result.setFullIndexingProcessId(fullIndexingSphinxProcess.getId());
        }
        ActivityLog deleteFullIndexLastLog = activityLogService.getLast(collectionName, DeleteFullIndexDataTask.TASK_NAME, null);
        ActivityLog indexLastLog = activityLogService.getLast(collectionName, MakeCollectionFullRebuildIndexTask.TASK_NAME, deleteFullIndexLastLog != null ? deleteFullIndexLastLog.getDate() : null);
        if(indexLastLog != null){
            result.setFullIndexingServer(indexLastLog.getServer());
            result.setIndexingTaskUid(indexLastLog.getTaskUid());
            switch (indexLastLog.getTaskStatus()) {
                case RUNNING:
                    result.setFullIndexingState(FullIndexingState.RUNNING);
                    break;
                case STOPPED:
                    result.setFullIndexingState(FullIndexingState.STOP);
                    break;
                case PAUSED:
                    result.setFullIndexingState(FullIndexingState.STOP);
                    break;
                case SUCCESS:
                    ActivityLog applyLastLog = activityLogService.getLast(collectionName, MakeCollectionFullRebuildApplyTask.TASK_NAME, indexLastLog.getDate());
                    if(applyLastLog == null){
                        result.setFullIndexingState(FullIndexingState.READY_FOR_APPLY);
                    }
                    else{
                        result.setApplyTaskUid(applyLastLog.getTaskUid());
                        switch (applyLastLog.getTaskStatus()) {
                            case RUNNING:
                                result.setFullIndexingState(FullIndexingState.IN_PROCESS);
                                break;
                            case STOPPED:
                                result.setFullIndexingState(FullIndexingState.ERROR_APPLY);
                                break;
                            case PAUSED:
                                result.setFullIndexingState(FullIndexingState.ERROR_APPLY);
                                break;
                            case SUCCESS:
                                result.setFullIndexingState(FullIndexingState.OK);
                                break;
                            case FAILURE:
                                result.setFullIndexingState(FullIndexingState.ERROR_APPLY);
                                break;
                        }
                    }
                    break;
                case FAILURE:
                    result.setFullIndexingState(FullIndexingState.ERROR);
                    break;
            }
        }

        return result;
    }

    @PostConstruct
    public void initIt() throws Exception {
        /**
         * Если при параллельной обработке включить PostConstruct - получаем deadlock
         * в методе org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(java.lang.String, boolean )
         * из-за того, что CollectionsInfoService не успеет создасться и заберёт себе монитор synchronized (this.singletonObjects)
         *  а новый поток, который в методе  ru.skuptsov.sphinx.console.coordinator.task.schedule.CollectionsInfoService#initIt() автоварит другие бины
         *  будет ждать когда кто-то этот монитор отпустит
         *  https://jira.spring.io/browse/SPR-8471
         *
         * Метод отработает по расписанию - необ-но его запускать до инициализации приложения
         *
         */
        if(!Boolean.valueOf(agentInfoParallel))
            process();
    }

}
