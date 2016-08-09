package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 27.05.15
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public interface SphinxQLConsoleService {

    List<String> getConsoleQueries(String query);

}
