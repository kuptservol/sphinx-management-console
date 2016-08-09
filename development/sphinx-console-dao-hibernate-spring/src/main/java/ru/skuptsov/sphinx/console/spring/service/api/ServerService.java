package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;

import java.util.List;

public interface ServerService {
    List<Server> getServers();
    List<Server> getServers(SphinxProcessType sphinxProcessType);
    Server getServer(String name);
    Server getServer(Long id);
    AdminProcess getAdminProcess(ProcessType type, String serverName);
    Server addServer(Server server) throws Throwable;
    void deleteServer(Long serverId);
    void deleteServer(String serverName);
    Server getServer(ProcessType type, String serverName);
    String getCoordinatorCallbackHost();
    String getCoordinatorCallbackPort();
    Server getCoordinator();
    Server getServer(SphinxProcessType type, String serverName);
    AdminProcess getAdminProcess(ProcessType type, Server server);
}
