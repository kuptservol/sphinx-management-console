package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface AdminProcessDao extends Dao<AdminProcess> {
	List<AdminProcess> getAdminProcesses(Long serverId);
    List<AdminProcess> getAdminProcesses(String serverName);
    AdminProcess getAdminProcess(ProcessType processType, String serverName);
    void deleteAdminProcessesByServer(Long serverId);
    void deleteAdminProcessesByServer(String serverName);
}
