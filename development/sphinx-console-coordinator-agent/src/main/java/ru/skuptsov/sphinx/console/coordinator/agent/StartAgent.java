package ru.skuptsov.sphinx.console.coordinator.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

/**
 * Стартовая точка старта агента
 * Created by SKuptsov on 27.03.15.
 */
public class StartAgent {
    private static final Logger logger = LoggerFactory.getLogger(StartAgent.class);

    public static void main(String[] args){

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        logger.info("Starting agent with params: " + Arrays.toString(arguments.toArray()));
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:root-context.xml");

    }
}
