package ru.skuptsov.sphinx.console.admin.service.impl;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import ru.skuptsov.sphinx.console.admin.model.CommandResult;
import ru.skuptsov.sphinx.console.admin.service.api.RestService;
import ru.skuptsov.sphinx.console.admin.service.api.StringService;
import ru.skuptsov.sphinx.console.coordinator.model.TaskRunningStatus;

@Service
public class RestServiceImpl implements RestService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${admin.coordinator.server.ip}")
    private String coordinatorServer;

    @Value("${admin.coordinator.server.port}")
    private String coordinatorPort;

    private static final String METHOD_PREFIX = "/sphinx.console-coordinator/rest/coordinator/";
    private static final String COORDINATOR_METHOD_PREFIX = METHOD_PREFIX + "configuration/";
    private static final String VIEW_METHOD_PREFIX = METHOD_PREFIX + "view/";

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private StringService stringService;

    private ClientResponse callCoordinatorRestMethod(String methodUrl, String type, String prefixJsonPath, String jsonFilePath, String jsonPropsFilePath) {
        return callRestMethod(COORDINATOR_METHOD_PREFIX, methodUrl, type, prefixJsonPath, jsonFilePath, jsonPropsFilePath);
    }

    private ClientResponse callViewRestMethod(String methodUrl, String type) {
        return callRestMethod(VIEW_METHOD_PREFIX, methodUrl, type, null, null, null);
    }

    private ClientResponse callWithRetry(final String methodPrefix, final String methodUrl, final String type, final String prefixJsonPath, final String jsonFilePath, final String jsonPropsFilePath) throws Throwable {
        logger.info(MessageFormat.format("ABOUT TO EXECUTE COORDINATOR METHOD, IN: {0}. For method: {1}. Request type: {2}", this, methodPrefix + methodUrl, type));
        return retryTemplate.execute(new RetryCallback<ClientResponse>() {
            public ClientResponse doWithRetry(RetryContext context) {
                return callRestNativeMethod(methodPrefix, methodUrl, type, prefixJsonPath, jsonFilePath, jsonPropsFilePath);
            }
        });
    }

    private ClientResponse callRestNativeMethod(String methodPrefix, String methodUrl, String type, String prefixJsonPath, String jsonFilePath, String jsonPropsFilePath) {
        ClientResponse response = null;

        logger.info("SyncCoordinatorExecutorCmd begin call REST method.");
        Client client = Client.create();

        String restUrl = "http://" + coordinatorServer + ":" + coordinatorPort +
                methodPrefix + methodUrl;

        logger.info("SyncCoordinatorExecutorCmd call REST url - '" + restUrl + "'.");

        WebResource webResource = client.resource(restUrl);

        if ("POST".equals(type.toUpperCase())) {
            if (jsonFilePath != null && jsonFilePath.length() > 0) {
                logger.info("prefixJsonPath - " + prefixJsonPath + ", jsonFilePath - '" + jsonFilePath + "', jsonPropsFilePath - '" + jsonPropsFilePath + "'.");
                String jsonString = stringService.getFormattedString(prefixJsonPath, jsonFilePath, jsonPropsFilePath);
                response = webResource.accept("application/json").type("application/json").post(ClientResponse.class, jsonString);
            } else {
                response = webResource.accept("application/json").post(ClientResponse.class);
            }
        } else if ("GET".equals(type.toUpperCase())) {
            response = webResource.accept("application/json").get(ClientResponse.class);
        } else if ("DELETE".equals(type.toUpperCase())) {
            response = webResource.accept("application/json").delete(ClientResponse.class);
        }

        return response;
    }

    private ClientResponse callRestMethod(String methodPrefix, String methodUrl, String type, String prefixJsonPath, String jsonFilePath, String jsonPropsFilePath) {
        ClientResponse response = null;
        try {
            response = callWithRetry(methodPrefix, methodUrl, type, prefixJsonPath, jsonFilePath, jsonPropsFilePath);
        } catch (Throwable throwable) {
            throw new RuntimeException("RestUtils: REST method call undefined error.", throwable);
        }

        if (response == null) {
            throw new RuntimeException("RestUtils: Method call type (post, get, delete) undefined.");
        }
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response;
    }

    private String getJsonValue(String jsonString, String name) {
        String value = null;
        Pattern pattern = Pattern.compile(name + ".*?:\\\"{0,1}(.*?)[\\\",]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(jsonString);
        if (matcher.find()) {
            value = matcher.group(1);
        }

        return value;
    }

    @Override
    public CommandResult callCoordinatorMethod(String methodUrl, String prefixJsonPath, String jsonFilePath, String jsonPropsFilePath, String type, boolean ignoreErrors) {
        ClientResponse response = callCoordinatorRestMethod(methodUrl, type, prefixJsonPath, jsonFilePath, jsonPropsFilePath);

        String output = response.getEntity(String.class);

        logger.info("Output from method - " + methodUrl + ":");
        logger.info(output);

        Integer code = getJsonValue(output, "code") != null ?  Integer.valueOf(getJsonValue(output, "code")) : -1;
        CommandResult commandResult =
            new CommandResult(output, getJsonValue(output, "taskUid"), code);

        if (commandResult.getCode() == 0) {
            if(commandResult.getTaskUid() != null && commandResult.getTaskUid().length() > 0) {
                int tryCount = 1;
                TaskRunningStatus taskRunningStatus = null;
                while (!TaskRunningStatus.COMPLETE.equals(taskRunningStatus) &&
                            !TaskRunningStatus.EMPTY.equals(taskRunningStatus) &&
                                !TaskRunningStatus.FAIL.equals(taskRunningStatus)) {
                    logger.info("RestUtils check task status trying: " + tryCount + ".");

                    response = callViewRestMethod("taskComplete/" + commandResult.getTaskUid(), "GET");
                    taskRunningStatus = TaskRunningStatus.valueOf(response.getEntity(String.class));
                    commandResult.setActiveLogQueryStatus(output);

                    if(!ignoreErrors && TaskRunningStatus.FAIL.equals(taskRunningStatus)) {
                        throw new RuntimeException("RestUtils failed active log. Check coordinator/agent DB.");
                    }

//                    response = callRestMethod("task/status/test_collection_simple_one_server", jsonFilePath);
//                    output = response.getEntity(String.class);

                    logger.info("RestUtils current status: " + output + ".");

                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        /*todo error*/
                        logger.error("RestServiceImpl Thread.currentThread() exception.", e);
                    }
                    tryCount++;
                }
            }
        } else {
            if(ignoreErrors){
                logger.warn("Command fail, but errors ignoring due to command attribute ignoreErrors=\"true\"");
            }
            else{
                logger.error("RestUtils error on calling method - " + methodUrl + ".");
            }
//            throw new RuntimeException("RestUtils error on calling method - " + methodUrl + ".");
        }

        return commandResult;
    }

    public void setCoordinatorPort(String coordinatorPort) {
        this.coordinatorPort = coordinatorPort;
    }

    public void setCoordinatorServer(String coordinatorServer) {
        this.coordinatorServer = coordinatorServer;
    }
}
