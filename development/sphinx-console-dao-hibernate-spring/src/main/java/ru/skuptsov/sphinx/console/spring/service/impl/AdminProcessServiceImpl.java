package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.dao.api.AdminProcessDao;
import ru.skuptsov.sphinx.console.spring.service.api.AdminProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

import java.util.List;

@Service
public class AdminProcessServiceImpl extends AbstractSpringService<AdminProcessDao, AdminProcess> implements AdminProcessService {

    @Autowired
    private ServerService serverService;

    @Override
    @Transactional(readOnly = true)
    public List<AdminProcess> getAdminProcesses(Long serverId) {
        return getDao().getAdminProcesses(serverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminProcess> getAdminProcesses(String serverName) {
        return getDao().getAdminProcesses(serverName);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProcess getAdminProcess(Long adminProcessId) {
        return findById(adminProcessId);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProcess getAdminProcess(ProcessType processType, String serverName) {
        return getDao().getAdminProcess(processType, serverName);
    }

    @Override
    @Transactional
    public AdminProcess addAdminProcess(AdminProcess adminProcess) throws Throwable {
        return save(adminProcess);
    }

    @Override
    @Transactional
    public void addAdminProcesses(List<AdminProcess> adminProcesses, Server server) throws Throwable {
        serverService.addServer(server);
        for(AdminProcess adminProcess : adminProcesses) {
            adminProcess.setServer(server);
            getDao().saveOrUpdate(adminProcess);
        }
    }

    @Override
    @Transactional
    public AdminProcess updateAdminProcess(AdminProcess adminProcess) throws Throwable {
        return save(adminProcess);
    }

    @Override
    @Transactional
    public void deleteAdminProcess(Long adminProcessId) throws Throwable {
        deleteById(adminProcessId);
    }

    @Override
    @Transactional
    public void deleteAdminProcessesByServer(Long serverId) throws Throwable {
        getDao().deleteAdminProcessesByServer(serverId);
    }

    @Override
    @Transactional
    public void deleteAdminProcessesByServer(String serverName) throws Throwable {
        getDao().deleteAdminProcessesByServer(serverName);
    }
}
