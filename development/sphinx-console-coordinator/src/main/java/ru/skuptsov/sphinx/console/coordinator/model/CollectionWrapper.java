package ru.skuptsov.sphinx.console.coordinator.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CollectionWrapper implements Serializable {

	private static final long serialVersionUID = 1345645L;
    @NotNull
    @Valid
    private SearchConfigurationPortWrapper searchConfigurationPort;
    private DistributedConfigurationPortWrapper distributedConfigurationPort;
    @NotNull
    @Valid
	private Configuration searchConfiguration;
    @NotNull
    @Valid
    private Configuration indexConfiguration;
    @NotNull
    @Valid
    private Server searchServer;
    @NotNull
    @Valid
    private Server indexServer;
    private Integer indexServerPort;
    @NotNull
    @Valid
    private Collection collection;
    @NotNull
    @Valid
    private CronScheduleWrapper cronSchedule;
    @Valid
    private CronScheduleWrapper mainCronSchedule;
    private String tableName;
    @Valid
    private Server fullIndexingServer;

    private CollectionInfoWrapper collectionInfo;
    
    private CollectionRoleType collectionType;

    public Configuration getSearchConfiguration() {
        return searchConfiguration;
    }

    public void setSearchConfiguration(Configuration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    public Configuration getIndexConfiguration() {
        return indexConfiguration;
    }

    public void setIndexConfiguration(Configuration indexConfiguration) {
        this.indexConfiguration = indexConfiguration;
    }

	public Server getSearchServer() {
		return searchServer;
	}

	public void setSearchServer(Server searchServer) {
		this.searchServer = searchServer;
	}

	public Server getIndexServer() {
		return indexServer;
	}

	public void setIndexServer(Server indexServer) {
		this.indexServer = indexServer;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

    public CronScheduleWrapper getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(CronScheduleWrapper cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public Integer getIndexServerPort() {
        return indexServerPort;
    }

    public void setIndexServerPort(Integer indexServerPort) {
        this.indexServerPort = indexServerPort;
    }

    public SearchConfigurationPortWrapper getSearchConfigurationPort() {
        return searchConfigurationPort;
    }

    public void setSearchConfigurationPort(SearchConfigurationPortWrapper searchConfigurationPort) {
        this.searchConfigurationPort = searchConfigurationPort;
    }

    public CronScheduleWrapper getMainCronSchedule() {
        return mainCronSchedule;
    }

    public void setMainCronSchedule(CronScheduleWrapper mainCronSchedule) {
        this.mainCronSchedule = mainCronSchedule;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Server getFullIndexingServer() {
        return fullIndexingServer;
    }

    public void setFullIndexingServer(Server fullIndexingServer) {
        this.fullIndexingServer = fullIndexingServer;
    }

    public CollectionInfoWrapper getCollectionInfo() {
        return collectionInfo;
    }

    public void setCollectionInfo(CollectionInfoWrapper collectionInfo) {
        this.collectionInfo = collectionInfo;
    }

	public DistributedConfigurationPortWrapper getDistributedConfigurationPort() {
		return distributedConfigurationPort;
	}

	public void setDistributedConfigurationPort(
			DistributedConfigurationPortWrapper distributedConfigurationPort) {
		this.distributedConfigurationPort = distributedConfigurationPort;
	}

	public CollectionRoleType getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(CollectionRoleType collectionType) {
		this.collectionType = collectionType;
	}
    
    
}
