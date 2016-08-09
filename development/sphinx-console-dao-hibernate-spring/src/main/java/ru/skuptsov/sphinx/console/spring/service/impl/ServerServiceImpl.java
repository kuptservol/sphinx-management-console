package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.dao.api.ServerDao;
import ru.skuptsov.sphinx.console.spring.service.api.AdminProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

import java.util.List;

@Service
public class ServerServiceImpl extends AbstractSpringService<ServerDao, Server> implements ServerService {

    @Autowired
    private AdminProcessService adminProcessService;
    
    private String coordinatorCallbackHost = "127.0.0.1";
	
	@Value("${jmx.coordinator.callback.port}")
	private String coordinatorCallbackPort;
	

	@Override
	@Transactional(readOnly = true)
	public List<ru.skuptsov.sphinx.console.coordinator.model.Server> getServers() {
		return getDao().getServers();
	}

    @Override
    @Transactional(readOnly = true)
    public List<ru.skuptsov.sphinx.console.coordinator.model.Server> getServers(SphinxProcessType sphinxProcessType) {
        return getDao().getServers(sphinxProcessType);
    }

    @Override
    @Transactional(readOnly = true)
    public Server getServer(String name) {
        return getDao().getServer(name);
    }

    @Override
	@Transactional
	public Server addServer(Server server) throws Throwable {
		return save(server);
	}

    @Override
    @Transactional
    public void deleteServer(Long serverId) {
        deleteById(serverId);
    }

	@Override
    @Transactional
    public void deleteServer(String serverName) {
        delete(getDao().getServer(serverName));
    }

    @Override
    @Transactional
    public AdminProcess getAdminProcess(ProcessType type, String serverName) {
        return adminProcessService.getAdminProcess(type, serverName);
    }

	@Override
	@Transactional(readOnly = true)
	public Server getServer(ProcessType type, String serverName) {
        AdminProcess adminProcess = adminProcessService.getAdminProcess(type, serverName); 
		 
		if (adminProcess != null)  {
	        return adminProcess.getServer();
	    }
		
		return null;
		 
	}

	@Override
	@Transactional(readOnly = true)
	public String getCoordinatorCallbackHost() {
		AdminProcess adminProcess = adminProcessService.getAdminProcess(ProcessType.COORDINATOR, null);
		Server server = null;
		
		if (adminProcess != null)  {
			server = adminProcess.getServer();
		}
		
		if (server != null) {
			return server.getIp();
		} 
		
		return coordinatorCallbackHost;
		
	}

	@Override
	@Transactional(readOnly = true)
	public String getCoordinatorCallbackPort() {
	    AdminProcess adminProcess = adminProcessService.getAdminProcess(ProcessType.COORDINATOR, null); // коордиратор всегда один. - поэтому null
		if (adminProcess != null) {
		    return adminProcess.getPort() + "";
		}
		
		
		return coordinatorCallbackPort;
		
	}

	@Override
	@Transactional(readOnly = true)
	public Server getServer(SphinxProcessType type, String serverName) {
        ProcessType processType = null;
        
        if (type == null) {
        	processType = ProcessType.COORDINATOR;
        } else if (type == SphinxProcessType.SEARCHING) {
        	processType = ProcessType.SEARCH_AGENT;
        } else if (type == SphinxProcessType.INDEXING) {
        	processType = ProcessType.INDEX_AGENT;
        }
        
        AdminProcess adminProcess = adminProcessService.getAdminProcess(processType, serverName); 
        
        if (adminProcess != null)  {
        	return adminProcess.getServer();
        }
        
        return null;
	}

	@Override
	@Transactional(readOnly = true)
	public AdminProcess getAdminProcess(ProcessType type, Server server) {
		return adminProcessService.getAdminProcess(type, server.getName());
	}

	@Override
	@Transactional(readOnly = true)
	public Server getServer(Long id) {
		return getDao().findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Server getCoordinator() {
		ProcessType processType = ProcessType.COORDINATOR;
		
		AdminProcess adminProcess = adminProcessService.getAdminProcess(processType, null); 
	        
	    if (adminProcess != null)  {
	        return adminProcess.getServer();
	    }
		
		return null;
	}
}
