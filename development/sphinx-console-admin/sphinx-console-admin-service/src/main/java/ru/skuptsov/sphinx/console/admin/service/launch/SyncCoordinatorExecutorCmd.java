package ru.skuptsov.sphinx.console.admin.service.launch;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import ru.skuptsov.sphinx.console.admin.service.api.ChangesetService;
import ru.skuptsov.sphinx.console.admin.service.api.RestService;


/**
 * Created by Developer on 10.03.2015.
 */
public class SyncCoordinatorExecutorCmd {

    private static final String CHANGESET_FILE_PARAM = "changeset";
    private static final String CHANGESET_FILE_PROPS = "props";
    private static final String IS_ROLLBACK = "rollback";
    private static final String REST_IP = "restIp";
    private static final String REST_PORT = "restPort";
    private static final String ADMIN_JDBC_URL = "jdbcUrl";


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SyncCoordinatorExecutorCmd syncCoordinatorExecutorCmd =
                new SyncCoordinatorExecutorCmd();
        syncCoordinatorExecutorCmd.process(args);
    }

    public void process(String[] args) {
        logger.info("SyncCoordinatorExecutorCmd begin.");
        try {

            ApplicationContext context =
                    new ClassPathXmlApplicationContext("classpath:sphinx.console-admin-service-context.xml");

            String changesetFilePath = "", changesetPropsFilePath = "";
            boolean rollback = false;
            CommandLinePropertySource cmdProperties = new SimpleCommandLinePropertySource(args);
            //parse arguments
            if (args != null) {
                if (args.length > 0) {
                    if (cmdProperties.containsProperty(CHANGESET_FILE_PARAM)) {
                        changesetFilePath = cmdProperties.getProperty(CHANGESET_FILE_PARAM);
                    } else {
                        String changesetFilePathNotFoundMessage = "Mandatory param changesetFilePath --" + CHANGESET_FILE_PARAM + " arg is not set";
                        logger.error(changesetFilePathNotFoundMessage);
                        System.exit(1);
                    }

                    if (cmdProperties.containsProperty(CHANGESET_FILE_PROPS))
                        changesetPropsFilePath = cmdProperties.getProperty(CHANGESET_FILE_PROPS);

                    logger.info("SyncCoordinatorExecutorCmd changesetFilePath - '" + changesetFilePath +
                            "', changesetPropsFilePath - '" + changesetPropsFilePath + "'.");

                    if (cmdProperties.containsProperty(IS_ROLLBACK)) {
                        rollback = Boolean.valueOf(cmdProperties.getProperty(IS_ROLLBACK));
                        logger.info("SyncCoordinatorExecutorCmd do rollback - " + String.valueOf(rollback) + ".");
                    }

                    RestService restService = context.getBean(RestService.class);
                    if (cmdProperties.containsProperty(REST_IP)) {
                        restService.setCoordinatorServer(cmdProperties.getProperty(REST_IP));
                    }

                    if (cmdProperties.containsProperty(REST_PORT)) {
                        restService.setCoordinatorPort(cmdProperties.getProperty(REST_PORT));
                    }

                    if (cmdProperties.containsProperty(ADMIN_JDBC_URL)) {

                        BasicDataSource adminDataSource = (BasicDataSource) context.getBean("adminDataSource");
                        adminDataSource.setUrl(cmdProperties.getProperty(ADMIN_JDBC_URL));
                    }
                }
            } else {
                String noArgumentErrorMessage = "SyncCoordinatorExecutorCmd args is not set or more than one.";
                logger.error(noArgumentErrorMessage);
                System.exit(1);
            }


            ChangesetService changesetRepository = context.getBean(ChangesetService.class);

            if (!rollback) {
                changesetRepository.execute(changesetFilePath, changesetPropsFilePath, true);
            } else {
                changesetRepository.rollback(changesetFilePath, changesetPropsFilePath);
            }
        } catch (Throwable thr)
        {
            logger.error("SyncCoordinatorExecutorCmd execution error.", thr);
            System.exit(1);
        }

        logger.info("SyncCoordinatorExecutorCmd end.");
    }
}

