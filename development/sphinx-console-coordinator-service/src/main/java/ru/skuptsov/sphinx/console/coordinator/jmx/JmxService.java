package ru.skuptsov.sphinx.console.coordinator.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

import java.text.MessageFormat;

/**
 * Created by lnovikova on 06.11.2015.
 */
@Component
public class JmxService {

    public static final Logger logger = LoggerFactory.getLogger(JmxService.class);
    public static final String CONNECTOR_URL_TEMPLATE = "service:jmx:rmi://{0}/jndi/rmi://{0}:{1}/";
    public static final String COORDINATOR_AGENT_CONNECTOR_URL_TEMPLATE = CONNECTOR_URL_TEMPLATE + "coordinatorAgentConnector";
    public static final String COORDINATOR_CALLBACK_CONNECTOR_URL_TEMPLATE = CONNECTOR_URL_TEMPLATE + "coordinatorCallbackConnector";

    private String localhost = "127.0.0.1";

    @Value("${jmx.agent.port}")
    private String agentPort;

    @Value("${jmx.coordinator.callback.port}")
    private String coordinatorCallbackPort;

    @Autowired
    protected ServerService serverService;

    public String getConnectorUrl(String host, Integer port, ProcessType processType){
        String serviceUrl = MessageFormat.format(processType == ProcessType.COORDINATOR ? COORDINATOR_CALLBACK_CONNECTOR_URL_TEMPLATE : COORDINATOR_AGENT_CONNECTOR_URL_TEMPLATE, host, port.toString());
        logger.debug("Service url: " + serviceUrl);
        return serviceUrl;
    }

    public String getConnectorUrl(AdminProcess process){
        return getConnectorUrl(process.getServer().getIp(), process.getPort(), process.getType());
    }

    public void setTaskAgentAddress(Task task, AdminProcess process){
        setTaskAgentAddress(task, process.getServer().getIp(), process.getPort(), process.getType());
    }

    public void setTaskCoordinatorAddress(Task task){

        String callbackHost = serverService.getCoordinatorCallbackHost();
        String callbackPort = serverService.getCoordinatorCallbackPort();

        if (callbackHost == null) {
            callbackHost = localhost;
        }

        if (callbackPort == null) {
            callbackPort = coordinatorCallbackPort;
        }

        String serviceUrl = getConnectorUrl(callbackHost, new Integer(callbackPort), ProcessType.COORDINATOR);
        logger.debug(MessageFormat.format("SetCoordinatorAddress: {0} For task: {1}", serviceUrl, task));
        task.setCoordinatorAddress(serviceUrl);
    }

    public void setReplicaAgentAddress(Replica replica, AdminProcess process){
        String address = getConnectorUrl(process);
        logger.debug(MessageFormat.format("SetSearchAgentAddress: {0} For replica : {1}", address, replica));
        replica.setSearchAgentAddress(address);
    }

    private void setTaskAgentAddress(Task task, String host, Integer port, ProcessType processType){
        String address = getConnectorUrl(host, port, processType);
        switch (processType){
            case COORDINATOR: {
                throw new ApplicationException("Improper use of method setTaskAgentAddress for setting Agent address for coordinator process.");
            }
            case INDEX_AGENT: {
                logger.debug(MessageFormat.format("SetIndexAgentAddress: {0} For task : {1}", address, task));
                task.setIndexAgentAddress(address);
                break;
            }
            case SEARCH_AGENT: {
                logger.debug(MessageFormat.format("SetSearchAgentAddress: {0} For task : {1}", address, task));
                task.setSearchAgentAddress(address);
                break;
            }
            default: {
                throw new ApplicationException("Improper use of method setTaskAgentAddress. Can't find handler for admin process type: " + processType);
            }
        }
    }

    public void setTaskSearchAgentLocalAddress(Task task){
        setTaskAgentAddress(task, localhost, new Integer(agentPort), ProcessType.SEARCH_AGENT);
    }

    public void setTaskIndexAgentLocalAddress(Task task){
        setTaskAgentAddress(task, localhost, new Integer(agentPort), ProcessType.INDEX_AGENT);
    }
}
