package ru.skuptsov.sphinx.console.coordinator.task.schedule.info;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.health.ServerHealthChecker;
import ru.skuptsov.sphinx.console.coordinator.jmx.JmxService;
import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.ServerInfoWrapper;
import ru.skuptsov.sphinx.console.coordinator.model.ServerStatus;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Component
public class ServersInfoService {
	private static final Logger logger = LoggerFactory.getLogger(ServersInfoService.class);
	
	@Autowired
	protected ServerService serverService;
	@Autowired
	private JmxService jmxService;
    
	
	@Resource
    protected ConcurrentHashMap<String, ServerInfoWrapper> serversInfoMap;
    
	@Scheduled(fixedDelayString = "${query.servers.info.delay}")
	public void process() {
		logger.info("ABOUT TO PROCESS CACHE OF SERVERS STATUSES...");
		
		List<Server> servers = serverService.getServers();
		
		for (Server server : servers) {
			logger.info("GET STATUS FOR SERVER: " + server.getName());
			
			if (serversInfoMap.get(server.getName()) == null) {
				serversInfoMap.put(server.getName(), new ServerInfoWrapper());	
			}
			
			AdminProcess coordinatorAdminProcess = serverService.getAdminProcess(ProcessType.COORDINATOR, server);
	        AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
	        AdminProcess indexAdminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, server);
			
			
	        if (coordinatorAdminProcess == null && searchAdminProcess == null && indexAdminProcess == null) {
	        	serversInfoMap.get(server.getName()).setServerStatus(ServerStatus.STOPPED);
	        	continue;
	        }
			
			if (searchAdminProcess != null ) {
				String searchAgentAddress = jmxService.getConnectorUrl(searchAdminProcess);
				ServerHealthChecker serverHealthChecker = (ServerHealthChecker)ApplicationContextProvider.getBean("serverHealthChecker");
				if (!serverHealthChecker.checkHealth(searchAgentAddress)) {
					serversInfoMap.get(server.getName()).setServerStatus(ServerStatus.STOPPED);
					continue;
				}
			} 
			
	        
			if (indexAdminProcess != null ) {
				String indexAgentAddress = jmxService.getConnectorUrl(indexAdminProcess);
				ServerHealthChecker serverHealthChecker = (ServerHealthChecker)ApplicationContextProvider.getBean("serverHealthChecker");
				if (!serverHealthChecker.checkHealth(indexAgentAddress)) {
					serversInfoMap.get(server.getName()).setServerStatus(ServerStatus.STOPPED);
					continue;
				}
			} 
			
			serversInfoMap.get(server.getName()).setServerStatus(ServerStatus.RUNNING);
		}
		
	}
	
	
}
