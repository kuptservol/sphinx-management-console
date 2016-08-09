package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component("MERGE_ACTION")
@Scope("prototype")
public class MergeActionCommand extends DbCommand<Task> {
	@Autowired
    private CollectionService collectionService;

	
	@Override
    @SaveActivityLog
    public Status execute(Task task) {
        logger.debug("MERGE_ACTION STATE EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        
        Collection collection = collectionService.getCollection(task.getCollectionName());
        
        logger.info("ABOUT TO PREPARE TO EXECUTE MERGE ACTION. FOR COLLECTION: " + collection);
        
        Delta delta =  collection.getDelta();
        
        if (delta != null) {
        	ExternalAction externalAction = delta.getExternalAction();
        	if (externalAction != null) {
        		if (externalAction.getType() == ExternalActionType.SQL) {
        		    runSQLAction(externalAction, status, task);	
        		}
        	}
        }
        
        
        return status;
    }
	
	private void runSQLAction(ExternalAction externalAction, Status status, Task task) {
	    DataSource datasource = externalAction.getDataSource();	
	
        org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("checkDataSource");
        String sql = externalAction.getCode();
		
		dataSource.setDriverClassName(datasource.getType().getDriverClass());
		dataSource.setUrl(datasource.getType().getUrl(datasource));
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection(datasource.getUser(), datasource.getPassword());
			statement = connection.createStatement();
			
			statement.execute(sql);
		} catch (SQLException e) {
			logger.error("could not connect to DB: ", e);
			status = Status.build(Status.SystemInterface.MERGE_ACTION_DB, Status.StatusCode.MERGE_ACTION_FAILURE, task.getTaskUID(), e);
			task.setStatus(status);
		} finally {
			try {
				if (connection != null) {
				    connection.close();
				}
			} catch (SQLException e) {
				
			}
		}	
			
	}
}
