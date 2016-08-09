package ru.skuptsov.sphinx.console.util.spring;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.access.MBeanProxyFactoryBean;

public class AgentMonitoringClientsPoolFactory extends BaseKeyedPoolableObjectFactory<String, org.springframework.jmx.access.MBeanProxyFactoryBean> {
	
	protected static final Logger logger = LoggerFactory.getLogger(AgentMonitoringClientsPoolFactory.class);
	
    
	@Override
	public MBeanProxyFactoryBean makeObject(String serviceUrl) throws Exception {
		logger.debug("ABOUT TO CREATE OBJECT IN POOL: " +  serviceUrl);
		
		MBeanProxyFactoryBean coordinatorAgentMonitoringServiceClient = new MBeanProxyFactoryBean();
		
		coordinatorAgentMonitoringServiceClient.setObjectName("coordinator.agent.mbeans:type=config,name=CoordinatorAgentMonitoringService");
		coordinatorAgentMonitoringServiceClient.setProxyInterface(ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.CoordinatorAgentMonitoringService.class);
		coordinatorAgentMonitoringServiceClient.setConnectOnStartup(false);
		coordinatorAgentMonitoringServiceClient.setRefreshOnConnectFailure(true);
		
		coordinatorAgentMonitoringServiceClient.setServiceUrl(serviceUrl);
		
		coordinatorAgentMonitoringServiceClient.afterPropertiesSet();
		
		
		logger.debug("COORDINATOR AGENT MONITORING SERVICE OBJECT TYPE: " + coordinatorAgentMonitoringServiceClient.getObjectType());
		
		return coordinatorAgentMonitoringServiceClient;
	}
	
	@Override
	public boolean validateObject(String serviceUrl, MBeanProxyFactoryBean bean) {
		logger.debug("ABOUT TO VALIDATE OBJECT IN POOL: " +  serviceUrl +  ", " +  bean + ", " +  super.validateObject(serviceUrl, bean));
		
		
		return super.validateObject(serviceUrl, bean);
	}
	
	@Override
	public void destroyObject(String serviceUrl, MBeanProxyFactoryBean bean) {
		logger.debug("ABOUT TO DESTROY OBJECT IN POOL: " +  serviceUrl +  ", " +  bean);
		
		try {
			bean.destroy();
			super.destroyObject(serviceUrl, bean);
		} catch (Throwable e) {
			logger.error("Error during destroyObject for agent monitoring clients pool", e);
		}
	}
	
	@Override
	public void passivateObject(String serviceUrl, MBeanProxyFactoryBean bean) {
		logger.debug("ABOUT TO PASSIVATE OBJECT IN POOL: " +  serviceUrl +  ", " +  bean);
		
		try {
			super.passivateObject(serviceUrl, bean);
		} catch (Throwable e) {
			logger.error("Error during passivateObject for agent monitoring clients pool", e);
		}
	}

}
