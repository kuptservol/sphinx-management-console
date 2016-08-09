package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;

import java.util.List;

public interface AdminProcessService {
    List<AdminProcess> getAdminProcesses(Long serverId);
    List<AdminProcess> getAdminProcesses(String serverName);
    AdminProcess getAdminProcess(Long adminProcessId);
    AdminProcess getAdminProcess(ProcessType processType, String serverName);
    AdminProcess addAdminProcess(AdminProcess adminProcess) throws Throwable;
    void addAdminProcesses(List<AdminProcess> adminProcesses, Server server) throws Throwable;
    AdminProcess updateAdminProcess(AdminProcess adminProcess) throws Throwable;
    void deleteAdminProcess(Long adminProcessId) throws Throwable;
    void deleteAdminProcessesByServer(Long serverId) throws Throwable;
    void deleteAdminProcessesByServer(String serverName) throws Throwable;
}
