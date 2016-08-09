package ru.skuptsov.sphinx.console.snippet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.exception.SnippetProcessingStopException;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfigurationField;
import ru.skuptsov.sphinx.console.snippet.model.Item;
import ru.skuptsov.sphinx.console.snippet.model.Snippet;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Scope("prototype")
public class SnippetService {
	private static Logger logger = LoggerFactory.getLogger(SnippetService.class);
	
	@Resource
    protected ConcurrentHashMap<String, Boolean> snippetsProcessingMap;
	
	private List<String> fieldNames = new LinkedList<String>();
	
	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

    private boolean stop(String taskUID) {
    	Boolean processingFlag = snippetsProcessingMap.get(taskUID);
    	if (processingFlag != null && !processingFlag) {
    		return true;
    	}
    	return false;
    }

	@Transactional
	public void process(SnippetConfiguration configuration, boolean fullRebuild, int fetchSize, String baseFolder, String taskUID) {
		logger.info("ABOUT TO START PROCESSING SNIPPETS...");
		
		if (stop(taskUID)) return;
		
		org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = ApplicationContextProvider.getBean("snippetDataSource");
		
		dataSource.setDriverClassName(configuration.getDataSource().getType().getDriverClass());
		dataSource.setUrl(configuration.getDataSource().getType().getUrl(configuration.getDataSource()));
		
		dataSource.setPassword(configuration.getDataSource().getPassword());
		dataSource.setUsername(configuration.getDataSource().getUser());
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setFetchSize(fetchSize);
		
		Set<SnippetConfigurationField> fields = configuration.getFields();
		
		for (SnippetConfigurationField field : fields) {
			fieldNames.add(field.getFieldName());	
		}
		
		String [] preQueries = !fullRebuild?configuration.getPreQuery().split(";"):configuration.getFullPreQuery().split(";");
		String [] postQueries = !fullRebuild?configuration.getPostQuery().split(";"):configuration.getFullPostQuery().split(";");
		String mainQuery = !fullRebuild?configuration.getMainQuery():configuration.getFullMainQuery();
		
		if (stop(taskUID)) return;
		queries(jdbcTemplate, preQueries);
		if (stop(taskUID)) return;
		try {
			mainQuery(jdbcTemplate, mainQuery, fetchSize, baseFolder, taskUID);
		} catch (SnippetProcessingStopException e) {
			// stop processing
			return;
		}
		if (stop(taskUID)) return;
		queries(jdbcTemplate, postQueries);
	}
	
	private void queries(JdbcTemplate jdbcTemplate, String [] preQueries) {
		for (String sql : preQueries) {
			if(!StringUtils.isEmpty(sql)){
				jdbcTemplate.execute(sql);
			}
		}
	}
	
	private void mainQuery(JdbcTemplate jdbcTemplate, String query, int fetchSize, final String baseFolder, final String taskUID) {
		logger.info("ABOUT TO EXECUTE MAIN QUERY...");
		
		
		jdbcTemplate.query(new StreamingStatementCreator(query, fetchSize), new RowCallbackHandler() {
		      public void processRow(ResultSet resultSet) throws SQLException { 
		    	  if (stop(taskUID)) throw new SnippetProcessingStopException();
		    	  Snippet snippet = new Snippet();  

		          snippet.setId(resultSet.getLong(1)); // ID field always should go first  

		          for (String fieldName : fieldNames) {
		              snippet.getItems().add(new Item(fieldName, resultSet.getString(fieldName)));
		          }
		        	 
		          createSnippet(snippet, baseFolder);
		      }
		    });
	}
	
	private void createSnippet(Snippet snippet, String baseFolder) {
		logger.info("SNIPPET, ID: " + snippet.getId());	
	    
	    String[] subFolders = LevelType.getSubfolders(snippet.getId());
	    
	    for (Item item : snippet.getItems()) {
	    	if (item.getValue() == null) continue;
	    	String folder = baseFolder;
	    	
	    	String targetFolder = createFolder(folder, subFolders);
	    	
	    	BufferedWriter output = null;
	        try {
	            File path = new File(targetFolder + "/" + item.getFieldName());
	            if (!path.exists()) {
	                path.mkdirs();
	            }
	            output = new BufferedWriter(new FileWriter(new File(targetFolder + "/" + item.getFieldName() + "/" + snippet.getId() + ".txt")));
	            output.write(item.getValue());
	        } catch ( IOException e ) {
	           throw new ApplicationException(e);
	        } finally {
	            if ( output != null )
					try {
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage(), e);
					}
	        }
	    	
	        logger.info("TARGET FOLDER: " + targetFolder);
	    }
	}
	
	private String createFolder(String folder, String[] subFolders) {
		StringBuffer targetFolder = new StringBuffer(folder);
		
		for (String subFolder : subFolders) {
			targetFolder.append("/" + subFolder);
		}
		
		return targetFolder.toString();
	}
	
	
	class StreamingStatementCreator implements PreparedStatementCreator {
	    private final String sql;
	    private final int fetchSize;

	    public StreamingStatementCreator(String sql, int fetchSize) {
	        this.sql = sql;
	        this.fetchSize = fetchSize;
	    }

	    @Override
	    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	        final PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        statement.setFetchSize(fetchSize);
	        return statement;
	    }
	}
	
	private static enum LevelType {
		LEVEL_4(4, "000 000 000,000.###"),
		LEVEL_5(5, "000 000 000 000,000.###"),
		LEVEL_6(6, "000 000 000 000 000,000.###");
		
		private int level;
		private String pattern;
		
		private LevelType(int level, String pattern) {
			this.level = level;
			this.pattern = pattern;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getPattern() {
			return pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}
		
		public static String getSubfoldersString(Long id) {
			if (id == null) {
				return null;
			}
			String idStr = id.toString();
			
			DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		    symbols.setGroupingSeparator(' ');
		    DecimalFormat df = null; // 000 000,000.#### - level = 3, 000 000 000,000.### level = 4, 000 000 000 000,000.### level = 5
		    
			
			if (idStr.length() <=12) {
				df = new DecimalFormat(LevelType.LEVEL_4.getPattern(), symbols);
			} else if (idStr.length() > 12) {
				df = new DecimalFormat(LevelType.LEVEL_6.getPattern(), symbols);
			}
			
			return df.format(id);
		}
		
		public static String[] getSubfolders(Long id) {
			String subfoldersStr = getSubfoldersString(id);
			logger.info("FOLDER STR: " + subfoldersStr);
			String[] subfolders = subfoldersStr.split(" ");
			
			String[] targetSubfolders = Arrays.copyOf(subfolders, subfolders.length - 1);
			
			for (String folder : targetSubfolders) {
				logger.info("FOLDER: " + folder);
			}
			
			return targetSubfolders;
		}
		
	}

}
