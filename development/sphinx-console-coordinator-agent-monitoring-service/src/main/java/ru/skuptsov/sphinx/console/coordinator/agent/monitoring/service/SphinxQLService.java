package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service;

import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 19.05.15
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public interface SphinxQLService {

    public static final String SET_PROFILING_QUERY = "set profiling=1";
    public static final String SHOW_PROFILE_QUERY = "show profile";
    public static final String SHOW_PLAN_QUERY = "show plan";
    public static final String SHOW_META_QUERY = "show meta";
    public static final String OPTION_KEY_WORD = "OPTION";
    public static final String sphinx.console_QUERY_COMMENT = "comment=\'sphinx.consoleQuery\'";
    public static final String sphinx.console_QUERY_OPTION_COMMENT = OPTION_KEY_WORD + " " + sphinx.console_QUERY_COMMENT;

    Long getCollectionSize(Integer port, Integer searchdPort, String collectionName);
    Boolean runQuery(Integer searchdPort, String collectionName);
    SphinxQLMultiResult getSphinxQLMultyQueryResult(Integer searchdPort, List<String> queries);
}
