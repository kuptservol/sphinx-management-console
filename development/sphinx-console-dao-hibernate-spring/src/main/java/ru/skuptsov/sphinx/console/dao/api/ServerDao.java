package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface ServerDao extends Dao<Server> {
	List<Server> getServers();
    Server getServer(String name);
    List<Server> getServers(SphinxProcessType sphinxProcessType);
}
