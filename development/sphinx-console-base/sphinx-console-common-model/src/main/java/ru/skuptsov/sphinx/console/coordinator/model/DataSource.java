package ru.skuptsov.sphinx.console.coordinator.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Port;

import javax.validation.constraints.NotNull;

public class DataSource extends BaseEntity {
	private Long id;

    private String name;
	@NotNull
    private DataSourceType type;
	@NotEmpty
    private String host;
	@NotNull
	@Port
    private Integer port;
	@NotEmpty
    private String user;
	@NotEmpty
    private String password;
    private String odbcDsn;
	@NotEmpty
    private String sqlDb;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOdbcDsn() {
        return odbcDsn;
    }

    public void setOdbcDsn(String odbcDsn) {
        this.odbcDsn = odbcDsn;
    }

	public DataSourceType getType() {
		return type;
	}

	public void setType(DataSourceType type) {
		this.type = type;
	}

	public String getSqlDb() {
		return sqlDb;
	}

	public void setSqlDb(String sqlDb) {
		this.sqlDb = sqlDb;
	}

	@Override  
	public int hashCode() {  
	    return new HashCodeBuilder()  
	         .append(this.host)  
	         .append(this.odbcDsn)
	         .append(this.password)
	         .append(this.port)
	         .append(this.sqlDb)
	         .append(this.type)
	         .append(this.user)
	         .toHashCode();
    }
	
	@Override
	public boolean equals(Object other) {
	      if (this == other) { return true; }
	      if ((other == null) || (other.getClass() != this.getClass())) { return false; }

	      DataSource castOther = (DataSource) other;
	      return new EqualsBuilder()
	    		  .append(this.host, castOther.getHost())
	    		  .append(this.odbcDsn, castOther.getOdbcDsn())
	    		  .append(this.password, castOther.getPassword())
	    		  .append(this.port, castOther.getPort())
	    		  .append(this.sqlDb, castOther.getSqlDb())
	    		  .append(this.type, castOther.getType())
	    		  .append(this.user, castOther.getUser())
	    		  .isEquals();
	}
}
