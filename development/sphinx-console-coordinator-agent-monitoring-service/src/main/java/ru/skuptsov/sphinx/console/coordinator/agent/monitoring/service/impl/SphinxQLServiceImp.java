package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLResultBuilder;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLResult;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SphinxQLServiceImp implements SphinxQLService {

    private static final Logger logger = LoggerFactory.getLogger(SphinxQLServiceImp.class);

    @Value("${connection.timeout.ms}")
    private int SPHINX_CONNECTION_TIMEOUT_MS;

    /**
     * Строка подключения к сфинксу
     */
    private String CONNECTION_STRING_TEMPLATE;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        CONNECTION_STRING_TEMPLATE = "jdbc:mysql://127.0.0.1:{0}/?useUnicode=yes&characterEncoding=UTF-8&connectTimeout="+SPHINX_CONNECTION_TIMEOUT_MS+"&allowMultiQueries=true&maxAllowedPacket=512000";
    }

    @Autowired
    SphinxQLResultBuilder sphinxQLResultBuilder;

    @Override
    @ManagedOperation(description = "")
    public Long getCollectionSize(Integer port, Integer searchdPort,  String collectionName) {
        logger.info("ABOUT TO RETRIEVE INDEX SIZE: " + collectionName);

        String sql = "SELECT * from " + collectionName + " limit 0,0 " + sphinx.console_QUERY_OPTION_COMMENT + ";";
        logger.info("SQL: " + sql);
        final String connectionString = MessageFormat.format(CONNECTION_STRING_TEMPLATE, String.valueOf(searchdPort));
        logger.info("CONNECTION_STRING: " + connectionString);

        DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("monitoringDataSource");

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(connectionString);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();

            if (stmt.execute(sql)) {
                do {
                    ResultSet rs = stmt.getResultSet();
                    rs.first();
                } while (stmt.getMoreResults());
            }

            ResultSet rs = stmt.executeQuery("SHOW META;");
            while (rs.next()) {
                try {
                    logger.info("META INFO: " + rs.getString("Variable_name") + ' ' + rs.getString("Value"));

                    if (rs.getString("Variable_name") != null && rs.getString("Variable_name").equals("total_found")) {
                        return rs.getLong("Value");
                    }

                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }


        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE RUN QUERY TO RETRIEVE COLLECTIOn SIZE: " + e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }


        return null;

    }

    @Override
    @ManagedOperation(description = "")
    public Boolean runQuery(Integer searchdPort, String collectionName) {
        logger.info("ABOUT TO RUN QUERY: " + collectionName);
        String sql = "SELECT * from " + collectionName + " limit 0,0 " + sphinx.console_QUERY_COMMENT + ";";
        logger.info("SQL: " + sql);
        final String connectionString = MessageFormat.format(CONNECTION_STRING_TEMPLATE, String.valueOf(searchdPort));

        DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("monitoringDataSource");

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(connectionString);
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();

            if (stmt.execute(sql)) {
                do {
                    ResultSet rs = stmt.getResultSet();
                    rs.first();
                } while (stmt.getMoreResults());
            }

            ResultSet rs = stmt.executeQuery("SHOW META;");
            while (rs.next()) {
                try {
                    logger.info("META INFO: " + rs.getString("Variable_name") + ' ' + rs.getString("Value"));

                    if (rs.getString("Variable_name") != null && rs.getString("Variable_name").equals("error")) {
                        return false;
                    }

                } catch (Throwable e) {
                    logger.error("Error during get sql results for meta info", e);
                }
            }


        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE RUN QUERY TO RETRIEVE COLLECTIOn SIZE: " + e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }

        return true;

    }

    @Override
    @ManagedOperation(description = "")
    public SphinxQLMultiResult getSphinxQLMultyQueryResult(Integer searchdPort, List<String> queries) {

        String queriesLogString = StringUtils.join(queries.toArray(), "; ");
        logger.debug("ABOUT TO EXECUTE SPHINXQL QUERIES: " + queriesLogString);
        final String connectionString = MessageFormat.format(CONNECTION_STRING_TEMPLATE, String.valueOf(searchdPort));

        DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("monitoringDataSource");

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(connectionString);
        Connection connection = null;

        SphinxQLMultiResult multiResult = new SphinxQLMultiResult();
        SphinxQLResult result = new SphinxQLResult();

        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();

            String mainQuery = queries.get(1).trim();
            if(mainQuery.toLowerCase().contains(OPTION_KEY_WORD.toLowerCase())){
                mainQuery = mainQuery.replaceAll("(?i)"+ Pattern.quote(OPTION_KEY_WORD), sphinx.console_QUERY_OPTION_COMMENT + ",");
            }else{
                String addSubstring = " " + sphinx.console_QUERY_OPTION_COMMENT;
                if(mainQuery.substring(mainQuery.length()-1,mainQuery.length()).equals(";")){
                    mainQuery = new StringBuilder(mainQuery).insert(mainQuery.length()-1, addSubstring).toString();
                }else{
                    mainQuery = mainQuery + addSubstring;
                }
            }
            queries.set(1,mainQuery);
            for(String query : queries){
                boolean isResultSetInResult = stmt.execute(query);
                if (isResultSetInResult) {
                    result = sphinxQLResultBuilder.buildResult(stmt.getResultSet());
                    multiResult.getResultList().add(result);
                }
                else{
                    // TODO lnovikova implement result building when no rs in result(if it is an update count or there are no results)
                }

            }

            multiResult.setStatus(Status.build(Status.SystemInterface.COORDINATOR_MONITORING, Status.StatusCode.SUCCESS_CODE));

        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE EXECUTING SPHINXQL QUERY: " + queriesLogString, e);
            result.setStatus(Status.build(Status.SystemInterface.COORDINATOR_MONITORING, Status.StatusCode.FAILURE_SPHINX_QL_COMMAND, e));
            multiResult.setStatus(Status.build(Status.SystemInterface.COORDINATOR_MONITORING, Status.StatusCode.FAILURE_SPHINX_QL_COMMAND, e));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error occurred during resource releasing for sphinxQL service for query: " + queriesLogString, e);
            }
        }

        return multiResult;

    }

}
