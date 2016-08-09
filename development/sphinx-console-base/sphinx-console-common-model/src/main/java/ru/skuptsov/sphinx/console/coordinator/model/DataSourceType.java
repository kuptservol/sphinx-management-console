package ru.skuptsov.sphinx.console.coordinator.model;

import java.text.MessageFormat;

public enum DataSourceType {

    MSSQL("net.sourceforge.jtds.jdbcx.JtdsDataSource", "jdbc:sqlserver://{0}:{1};databaseName={2}", "mssql"),
    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://{0}:{1}/{2}", "mysql"),
//    ODBC("sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:{0}", "odbc") {
//        public String getUrl(DataSource params) {
//            return MessageFormat.format(this.getUrlTemplate(), params.getOdbcDsn());
//        }
//    },
    ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@{0}:{1}/{2}", "oracle"),
    PGSQL("org.postgresql.Driver", "jdbc:postgresql://{0}:{1}/{2}", "pgsql");
	
	
    private String driverClass;
    private String urlTemplate;
    private String title;
    
    DataSourceType (String driverClass, String urlTemplate, String title) {
		this.driverClass = driverClass;
		this.urlTemplate = urlTemplate;
		this.title = title;
	}
	
	public String getDriverClass() {
		return driverClass;
	}
	
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}
	
	
	public String getUrl(DataSource params) {
		return MessageFormat.format(urlTemplate, params.getHost(), params.getPort().toString(), params.getSqlDb());
	}

    public String getUrl(String host, Integer port, String dbName) {
        return MessageFormat.format(urlTemplate, host, port.toString(), dbName);
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public static DataSourceType getByTitle(String title){
        DataSourceType result = null;
        for (DataSourceType dataSourceType : DataSourceType.values()){
            if(dataSourceType.getTitle().equals(title)){
                result = dataSourceType;
            }
        }
        return result;
    }
	
	
}