package ru.skuptsov.sphinx.console.snippet.test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.skuptsov.sphinx.console.coordinator.model.DataSource;
import ru.skuptsov.sphinx.console.coordinator.model.DataSourceType;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfigurationField;
import ru.skuptsov.sphinx.console.snippet.service.SnippetService;

public class Starter {
	public static void main(String[] args) {
        System.out.println("start context for snippet service...");
		Starter starter = new Starter();
		starter.run();

	}

	private void run() {

		String[] springConfig = { "sphinx.console-snippet-service-context.xml" };

		ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);

		SnippetService service = (SnippetService) context.getBean("snippetService");
		System.out.println("SNIPPET SERVICE: " + service);
		
		
		
		SnippetConfiguration configuration = new SnippetConfiguration();
		DataSource dataSource = new DataSource();
		dataSource.setPassword("root");
		dataSource.setUser("root");
		dataSource.setPort(3306);
		dataSource.setType(DataSourceType.MYSQL);
		dataSource.setSqlDb("sphinx.console");
		dataSource.setHost("192.168.211.111");
		
		
		configuration.setDataSource(dataSource);
		
		configuration.setPreQuery("select * from SERVER;select * from SERVER;select * from SERVER;");
		configuration.setPostQuery("select * from SERVER;select * from SERVER;select * from SERVER;");
		configuration.setMainQuery("select id, user_login from USERS where id < 30");
		
		SnippetConfigurationField field = new SnippetConfigurationField();
		field.setFieldName("user_login");
		configuration.getFields().add(field);
		
	/*	DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
	    symbols.setGroupingSeparator(' ');
	    DecimalFormat df = new DecimalFormat("000 000 000 000,000.###", symbols); // 000 000,000.#### - level = 3, 000 000 000,000.### level = 4, 000 000 000 000,000.### level = 5
	    String output1 = df.format(1L);
	    
	    String output2 = df.format(10L);
	    
	    String output3 = df.format(999999L);
	    
	    String output4 = df.format(100000L);
	    
	    String output5 = df.format(777888999L);
	    
	    String output6 = df.format(777887999L);
	    
	    String output7 = df.format(999999999999999999L);
	    
	    String output8 = df.format(99999999999999999L);
	    
	    String output9 = df.format(9999999999999999L);
	    
	    String output10 = df.format(999999999999999L);
	    
	    System.out.println("OUTPUT1: " + output1);
	    
	    System.out.println("OUTPUT2: " + output2);
	    
	    System.out.println("OUTPUT3: " + output3);
	    
	    System.out.println("OUTPUT4: " + output4);
	    
	    System.out.println("OUTPUT5: " + output5);
	    
	    System.out.println("OUTPUT6: " + output6);
	    
	    System.out.println("OUTPUT7: " + output7);
	    
	    System.out.println("OUTPUT8: " + output8);
	    
	    System.out.println("OUTPUT9: " + output9);
	    
	    System.out.println("OUTPUT10: " + output10);
	    
	    
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(1L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(1000L));
	    
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(2000L));
	    
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(999999999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(99999999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(9999999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(999999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(99999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(9999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(999999999999L));
	    System.out.println("OUTPUT 11: " + LevelType.getSubfolders(99999999999L));*/
		
		service.process(configuration, false, 10, "f:/snippet/test_1", "11111111");
		
		/*SqlParseService sqlParseService = (SqlParseService) context.getBean("sqlParseService");
		
		List<String> fields = sqlParseService.getSelectFields("select id, user_login as login, password as passwd from USERS");
		
		for (String field : fields) {
			System.out.println("FIELD: " + field);
		}*/
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
			System.out.println("FOLDER STR: " + subfoldersStr);
			String[] subfolders = subfoldersStr.split(" ");
			
			String[] targetSubfolders = Arrays.copyOf(subfolders, subfolders.length - 1);
			
			for (String folder : targetSubfolders) {
				System.out.println("FOLDER: " + folder);
			}
			
			return targetSubfolders;
		}
		
	}
}
