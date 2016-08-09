package ru.skuptsov.sphinx.console.coordinator.task.schedule.query.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.QueryLogService;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.search.query.SearchQueryProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ReplicaService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class QueryLogScheduleParseService {
	private static final Logger logger = LoggerFactory.getLogger(QueryLogScheduleParseService.class);

    @Autowired
    CollectionService collectionService;

    @Autowired
    ReplicaService replicaService;

    @Autowired
    ProcessService processService;

    @Autowired
    ServerService serverService;

    @Autowired
    private SearchQueryProcessService searchQueryProcessService;

    @Value("${query.log.enabled}")
    private String queryLogEnabled;

    @Value("${query.log.keep.day}")
    private String queryLogKeepDay;

	@Scheduled(fixedDelayString = "${query.log.parse.delay}")
    public void process() {
        if(new Boolean(queryLogEnabled)){
            logger.info("About to start parsing of query.log for all replicas of all collections...");

            List<Collection> collections = collectionService.getCollections();

            searchQueryProcessService.deleteOld(new Integer(queryLogKeepDay));

            for(Collection collection : collections){
                String collectionName = collection.getName();
                List<Replica> replicas = replicaService.findByCollectionName(collectionName);
                for(Replica replica : replicas){
                    Long replicaNumber = replica.getNumber();
                    SphinxProcess searchSphinxProcess = processService.findSearchProcess(collectionName, replicaNumber);
                    Server server = searchSphinxProcess.getServer();
                    AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);

                    MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

                    Date lastParsedDate = searchQueryProcessService.getLastParseDate(collectionName, replicaNumber);

                    Set<SearchQuery> searchQueries = monitoringService.getSearchQueriesResults(server.getIp(), adminProcess.getPort(),
                            collectionName, replicaNumber, lastParsedDate);
                    if(searchQueries == QueryLogService.ERROR_RESPONSE){
                        logger.error(MessageFormat.format("Error during parsing query.log for collection {0} replica {1}. " +
                                "Details in respective agent.log", collectionName, replicaNumber));
                    }
                    else {
                        if(searchQueries != QueryLogService.EMPTY_RESULT){
                            searchQueryProcessService.addMissingData(searchQueries, collection, replica);
                            searchQueryProcessService.saveQueries(searchQueries);
                        }
                    }
                }
            }
        }
	}
	
}
