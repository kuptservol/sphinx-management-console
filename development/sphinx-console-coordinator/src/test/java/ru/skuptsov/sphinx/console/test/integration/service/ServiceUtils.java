package ru.skuptsov.sphinx.console.test.integration.service;

import org.springframework.web.client.RestTemplate;

/**
 * Created by lnovikova on 07.11.2015.
 */
public class ServiceUtils {

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();
    public final String serverURI;

    ServiceUtils(String serverURI){
        this.serverURI = serverURI;
    }

}
