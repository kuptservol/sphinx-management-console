package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.QueryLogParseService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.QueryLogService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLService;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryResult;

import java.io.*;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 * Created by lnovikova on 16.09.2015.
 */
@Component
public class QueryLogServiceImp implements QueryLogService{

    protected static final Logger logger = LoggerFactory.getLogger(QueryLogServiceImp.class);
    private final Integer MAX_PARSING_FILES_COUNT = 2;
    private static Set<String> parsingCollections = new HashSet<String>();

    @Value("${base.dir}")
    private String baseDirPath;

    @Autowired
    QueryLogParseService queryLogParseService;

    /**
     *
     * @param collectionName
     * @param replicaNumber
     * @param lastParsedDate
     * @return ERROR_RESPONSE if any errors occurred
     * EMPTY_RESULT if no errors, but parsing can't be executed by some reasons
     * Set<SearchQuery> otherwise
     */
    @Override
    public Set<SearchQuery> getSearchQueriesResults(String collectionName, Long replicaNumber, Date lastParsedDate) {

        Date startDate = new Date();
        String logMessage = MessageFormat.format("Parsing query.log for collection {0} replicaNumber {1} after {2}.",
                collectionName, replicaNumber, lastParsedDate);
        logger.info(MessageFormat.format(logMessage +" Start time {0}", startDate));

        String collectionAndReplicaName = collectionName + "_" + replicaNumber;
        if(parsingCollections.contains(collectionAndReplicaName)){
            logger.warn(MessageFormat.format("Parsing of collection {0} is not finished yet. Abort parsing.", collectionName));
            return EMPTY_RESULT;
        }
        parsingCollections.add(collectionAndReplicaName);

        HashMap<String, Set<SearchQueryResult>> queryMap = new HashMap<String, Set<SearchQueryResult>>();

        ReversedLinesFileReader reader = null;
        boolean parsingFinished = false;
        int rotateNumber = 0;
        String logPath = "";
        try {

            for (rotateNumber = 0; rotateNumber < MAX_PARSING_FILES_COUNT && !parsingFinished; rotateNumber++) {
                logPath = getLogPath(collectionName, replicaNumber, rotateNumber);
                try {
                    reader = getReverseReader(logPath);
                    String line = reader.readLine();
                    while (line != null && !parsingFinished) {
                        // один лог может быть разбит на 2 строки, в этом случае считываем вторую строку и формируем целый лог
                        if(!line.substring(0, 2).equals("/*")){
                            line = reader.readLine() + line;
                        }
                        if (!line.contains(SphinxQLService.sphinx.console_QUERY_COMMENT)) {
                            if (lastParsedDate != null) {
                                Date date = queryLogParseService.parseDate(line);
                                if (date.getTime() > lastParsedDate.getTime()) {
                                    addResultToMap(queryMap, line);
                                }
                                else{
                                    parsingFinished = true;
                                }
                            } else {
                                addResultToMap(queryMap, line);
                            }
                        }
                        line = reader.readLine();
                    }
                }
                catch (FileNotFoundException fileNotFoundEx){
                    if(rotateNumber == 0){
                        logger.error("Can't find log file", fileNotFoundEx);
                        return ERROR_RESPONSE;
                    }
                    else {
                        logger.info("Rotated query.log file doesn't exists: " + logPath);
                    }
                }

            }

            Set<SearchQuery> result = buildSearchQueries(queryMap);
            Date stopDate = new Date();
            logger.info(MessageFormat.format(logMessage +" Stop time {0}. Duration in seconds", (startDate.getTime() - stopDate.getTime())/1000));
            return result;

        } catch(Throwable exc) {
            logger.error("Error during parsing query.log", exc);
            return ERROR_RESPONSE;
        } finally {
            parsingCollections.remove(collectionAndReplicaName);
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (Throwable exc) {
                logger.error("Error during closing reader",exc);
            }
        }
    }

    private Set<SearchQuery> buildSearchQueries(HashMap<String, Set<SearchQueryResult>> queryMap){
        Set<SearchQuery> queries = new LinkedHashSet<SearchQuery>();
        SearchQuery searchQuery;
        for(String query : queryMap.keySet()){
            searchQuery = new SearchQuery(query);
            Set<SearchQueryResult> results = queryMap.get(query);
            for(SearchQueryResult result : results){
                result.setSearchQuery(searchQuery);
            }
            searchQuery.setSearchQueryResults(results);
            queries.add(searchQuery);
        }

        return queries;
    }

    private String getLogPath(String collectionName, Long replicaNumber, Integer rotateIndex){
        return MessageFormat.format("{0}log/searching/{1}_{2}/query.log{3}", baseDirPath, collectionName, replicaNumber, rotateIndex > 0 ? "." + rotateIndex : "");
    }

    private Reader getReader(String absolutePath) throws FileNotFoundException, UnsupportedEncodingException {
        Reader reader;

        FileInputStream fstream1 = new FileInputStream(absolutePath);
        DataInputStream in = new DataInputStream(fstream1);
        reader = new InputStreamReader(in, "UTF-8");

        return reader;
    }

    private ReversedLinesFileReader getReverseReader(String absolutePath) throws IOException {

        ReversedLinesFileReader reader;
        File file = new File(absolutePath);
        reader = new ReversedLinesFileReader(file, 4096, "UTF-8");

        return reader;
    }

    private void addResultToMap(HashMap<String, Set<SearchQueryResult>> queryMap, String line) throws Exception {
        try{
            String query = getQueryFromLine(line);
            if (queryMap.get(query) == null) {
                queryMap.put(query, new LinkedHashSet<SearchQueryResult>());
            }
            Set<SearchQueryResult> results = queryMap.get(query);
            SearchQueryResult result = getSearchQueryResultFromLine(line);
            results.add(result);
        }catch (Exception e){
            logger.error(MessageFormat.format("Error during parsing line: {0}", line), e);
            throw e;
        }
    }

    private String getQueryFromLine(String line){
        String query = line.substring(line.indexOf("*/") + 2, line.lastIndexOf("/*") - 1);
        return query;
    }

    private SearchQueryResult getSearchQueryResultFromLine(String line) throws ParseException {
        SearchQueryResult result = new SearchQueryResult();
        result.setDate(queryLogParseService.parseDate(line));
        result.setTotalTime(queryLogParseService.parseTotalTime(line));
        result.setResultCount(queryLogParseService.parseResultCount(line));
        result.setOffset(queryLogParseService.parseOffSet(line));
        return result;
    }

}
