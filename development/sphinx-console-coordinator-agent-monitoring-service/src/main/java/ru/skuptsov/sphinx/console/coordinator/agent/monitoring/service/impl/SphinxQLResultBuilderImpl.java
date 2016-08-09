package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLResultBuilder;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 19.05.15
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SphinxQLResultBuilderImpl implements SphinxQLResultBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SphinxQLResultBuilderImpl.class);

    @Override
    public SphinxQLResult buildResult(ResultSet rs) throws SQLException{
        SphinxQLResult result = new SphinxQLResult();
        int columnCount = rs.getMetaData().getColumnCount();
        List<String> fields = new ArrayList<String>();
        for(int i = 1; i <= columnCount; i++){
            fields.add(rs.getMetaData().getColumnName(i));
        }
        result.setFields(fields);
        while (rs.next()) {
            List<String> row = new ArrayList<String>();
            for(int i = 1; i <= columnCount; i++){
                row.add(rs.getString(i));
            }
            result.addToResultList(row);
        }

        result.setStatus(Status.build(Status.SystemInterface.COORDINATOR_MONITORING, Status.StatusCode.SUCCESS_CODE));

        return result;

    }

}
