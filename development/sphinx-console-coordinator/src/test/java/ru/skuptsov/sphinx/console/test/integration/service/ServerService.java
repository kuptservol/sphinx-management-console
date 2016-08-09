package ru.skuptsov.sphinx.console.test.integration.service;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;

import java.util.*;

/**
 * Created by lnovikova on 06.11.2015.
 */
@Service
public class ServerService {

    protected static Logger logger = LoggerFactory.getLogger(ServerService.class);
    private static final int WAIT_TIME_LIMIT = 60000;
    private static final int WAIT_TIME_DELAY= 10000;


    @Autowired
    ServiceUtils serviceUtils;

    @Autowired
    ConverterService converterService;

    public void checkServersStatus(Set<Server> servers, ServerStatus expectedServerStatus){

        Map map = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.QUERY_SERVERS_INFO, Map.class);

        Map<String, ServerInfoWrapper> result = null;
        boolean serverNotFound = true;
        int waitTime = 0;
        while(serverNotFound && waitTime < WAIT_TIME_LIMIT) {
            result = converterService.convertResponseMap(map, String.class, ServerInfoWrapper.class);
            serverNotFound = false;
            for(Server server : servers){
                if(!result.containsKey(server.getName())) {
                    serverNotFound = true;
                    break;
                }
            }
            waitTime += WAIT_TIME_DELAY;
        }

        Assert.assertFalse("Can't find server in server info map during {0} milliseconds. Check ServerService.WAIT_TIME_LIMIT >= [query.servers.info.delay]. Test fails", serverNotFound);
        ServerStatus serverStatus;
        for(Server server : servers){
            serverStatus = result.get(server.getName()).getServerStatus();
                Assert.assertTrue("Server in status " + serverStatus + ". Expected to be in status " + expectedServerStatus,
                        serverStatus == expectedServerStatus);
        }

    }

    /**
     * Действие - создание сервера
     * Проверка
     * - процесс создания не вернул ошибок
     * - сервер нашёлся через rest
     *
     * @param serverName
     * @param serverIp
     */
    public Server createServer(String serverName, String serverIp) {
        Server createdServer = null;
        Server server = new Server();
        server.setName(serverName);
        server.setIp(serverIp);
        ResponseEntity<Status> responseEntity = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.ADD_SERVER, server, Status.class);
        logger.info("Responce code: " + responseEntity.getBody().getCode());
        Assert.assertEquals(0, responseEntity.getBody().getCode());

        Server[] servers = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SERVERS, Server[].class);

        for (Server serverFromRest : servers) {
            if (serverFromRest.getName().equals(serverName)) {
                Assert.assertEquals(serverIp, serverFromRest.getIp());
                createdServer = serverFromRest;
            }
        }

        Assert.assertNotNull(createdServer);

        return createdServer;
    }

    public void deleteServer(String serverName){
        ResponseEntity<Status> response = serviceUtils.REST_TEMPLATE.exchange(serviceUtils.serverURI +  CoordinatorConfigurationRestURIConstants.DELETE_SERVER_BY_NAME, HttpMethod.DELETE, null, Status.class, serverName);
        Assert.assertEquals("Status code expected be 0", 0, response.getBody().getCode());
        for(Server server : getServers()){
            Assert.assertNotEquals("Server still exists in coordinator DB", server.getName(), serverName);
        }
    }

    public Server[] getServers(){
        Server[] servers = serviceUtils.REST_TEMPLATE.getForObject(serviceUtils.serverURI + CoordinatorViewRestURIConstants.SERVERS, Server[].class);
        return servers;
    }
}
