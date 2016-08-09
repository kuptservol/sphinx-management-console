package ru.skuptsov.sphinx.console.coordinator.health;


import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;

@Component("serverHealthChecker")
@Scope("prototype")
public class ServerHealthChecker {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerHealthChecker.class);
	
	private static final int ALLOWED_CONNECTIONS_IN_POOL_FOR_PARTICULAR_AGENT = 1;
	
	private org.springframework.jmx.access.MBeanProxyFactoryBean coordinatorAgentServiceClient;
	
	@Autowired
	@Qualifier("agentClientsPool")
    private GenericKeyedObjectPool<String, org.springframework.jmx.access.MBeanProxyFactoryBean> agentClientsPool;
	
    private void getConnectionFromPool(String serviceUrl) {
    	long active = agentClientsPool.getNumActive(serviceUrl);
    	long idle = agentClientsPool.getNumIdle(serviceUrl);
    	
    	logger.debug("NUM ACTIVE: " + active);
    	logger.debug("NUM IDLE: " + idle);
    	
    
    	try {
    			
    		coordinatorAgentServiceClient = (MBeanProxyFactoryBean) agentClientsPool.borrowObject(serviceUrl);	
        	
    		active = agentClientsPool.getNumActive(serviceUrl);
    		
    		logger.debug("NUM ACTIVE AFTER BORROW: " + active);
    		
    		if (active >= ALLOWED_CONNECTIONS_IN_POOL_FOR_PARTICULAR_AGENT) {
    			agentClientsPool.returnObject(serviceUrl, coordinatorAgentServiceClient);		
    		}
    		
    		logger.debug("INSTANCE OF coordinatorAgentServiceClient: " + coordinatorAgentServiceClient);
    		
		} catch (Exception e) {
			logger.error("Error during getConnectionFromPool", e);
			throw new ApplicationException(e);
		}
    }
	
	public boolean checkHealth(String serviceUrl) {
		getConnectionFromPool(serviceUrl);
		
		
		CoordinatorAgentService coordinatorAgentService =  (CoordinatorAgentService) coordinatorAgentServiceClient.getObject();
		
		try {
		    coordinatorAgentService.test();
		} catch (Throwable e) {
			logger.error("Error during checkHealth", e);
			return false;
		}
		
		return true;
	}

}
