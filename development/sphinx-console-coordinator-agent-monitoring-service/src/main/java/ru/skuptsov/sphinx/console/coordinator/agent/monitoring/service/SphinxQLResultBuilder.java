package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service;

import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLResult;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 19.05.15
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public interface SphinxQLResultBuilder {
    SphinxQLResult buildResult(ResultSet rs) throws SQLException;
}
