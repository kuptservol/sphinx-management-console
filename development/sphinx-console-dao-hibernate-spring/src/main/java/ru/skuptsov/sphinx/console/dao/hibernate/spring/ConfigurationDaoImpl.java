package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationType;
import ru.skuptsov.sphinx.console.dao.api.ConfigurationDao;
import ru.skuptsov.sphinx.console.dao.common.hibernate.spring.util.HibernateInitializer;

import java.util.List;

@Repository
public class ConfigurationDaoImpl extends AbstractCoordinatorHibernateDao<Configuration> implements ConfigurationDao {
	private static final String CLEAR_FIELD_MAPPINGS_QUERY = "delete from sphinx.console.FIELD_MAPPING where CONFIGURATION_ID = :configuration_id";
	private static final String CLEAR_CONFIGURATION_FIELDS_QUERY = "delete from sphinx.console.CONFIGURATION_FIELDS where CONFIGURATION_TEMPLATE_ID = :configuration_template_id";
    private static final String CLEAR_SOURCE_CONFIGURATION_FIELDS_QUERY = "delete from sphinx.console.CONFIGURATION_FIELDS where CONFIGURATION_ID = :configuration_id and CONFIGURATION_TYPE = :configuration_type";
	
    @Override
    public Configuration getConfiguration(String name) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("configurationTemplate", "configurationTemplate", JoinType.INNER_JOIN);
        hbmCriteria.createAlias("searchConfigurationTemplate", "searchConfigurationTemplate", JoinType.INNER_JOIN);
        hbmCriteria.createAlias("indexerConfigurationTemplate", "indexerConfigurationTemplate", JoinType.LEFT_OUTER_JOIN);
        
        hbmCriteria.add(Restrictions.eq("name", name));
        Configuration configuration = (Configuration) hbmCriteria.uniqueResult();
        HibernateInitializer.initializeProperties(configuration, "fieldMappings",
        		"configurationTemplate", "configurationTemplate.configurationFields",
        		"searchConfigurationTemplate", "searchConfigurationTemplate.configurationFields",
        		"indexerConfigurationTemplate", "indexerConfigurationTemplate.configurationFields");
        return configuration;
    }

	@Override
	public void clearFieldMappings(Long configurationId) {
		SQLQuery query = getSession().createSQLQuery(CLEAR_FIELD_MAPPINGS_QUERY);
		query.setParameter("configuration_id", configurationId);
    	query.executeUpdate();
	}

	@Override
	public void clearConfigurationFields(Long configurationTemplateId) {
		SQLQuery query = getSession().createSQLQuery(CLEAR_CONFIGURATION_FIELDS_QUERY);
		query.setParameter("configuration_template_id", configurationTemplateId);
    	query.executeUpdate();
	}

    @Override
    public List<Configuration> getConfigurationByConfigurationTemplate(Long configurationTemplateId) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("configurationTemplate", "configurationTemplate", JoinType.LEFT_OUTER_JOIN);
        hbmCriteria.createAlias("searchConfigurationTemplate", "searchConfigurationTemplate", JoinType.LEFT_OUTER_JOIN);
        hbmCriteria.createAlias("indexerConfigurationTemplate", "indexerConfigurationTemplate", JoinType.LEFT_OUTER_JOIN);

        Criterion configurationTemplateConfigurationId = Restrictions.eq("configurationTemplate.id", configurationTemplateId);
        Criterion indexerConfigurationTemplateId = Restrictions.eq("indexerConfigurationTemplate.id", configurationTemplateId);
        Criterion searchConfigurationTemplateId = Restrictions.eq("searchConfigurationTemplate.id", configurationTemplateId);
        hbmCriteria.add(Restrictions.or(configurationTemplateConfigurationId,indexerConfigurationTemplateId,searchConfigurationTemplateId));
        List<Configuration> configurations = hbmCriteria.list();
        HibernateInitializer.initializeProperties(configurations, "fieldMappings",
                "configurationTemplate", "configurationTemplate.configurationFields",
                "searchConfigurationTemplate", "searchConfigurationTemplate.configurationFields",
                "indexerConfigurationTemplate", "indexerConfigurationTemplate.configurationFields");
        return configurations;
    }

	@Override
	public void clearSourceConfigurationFields(Long configurationId) {
		SQLQuery query = getSession().createSQLQuery(CLEAR_SOURCE_CONFIGURATION_FIELDS_QUERY);
		query.setParameter("configuration_id", configurationId);
		query.setParameter("configuration_type", ConfigurationType.SOURCE.toString());
    	query.executeUpdate();
		
	}

	@Override
	public List<Long> getConfigurationsIds(String collectionName) {
		    Criteria hbmCriteria = getSession().createCriteria(getPersistentClass()).setProjection( Projections.projectionList()
		            .add( Property.forName("id") ));

		    Criterion orCrit = Restrictions.or(Restrictions.like("name", "search-conf-" + collectionName + "-", MatchMode.START), Restrictions.like("name", "index-conf-" + collectionName, MatchMode.EXACT));
	        
	        
	        hbmCriteria.add(orCrit);
	        return hbmCriteria.list();
	        
	}
}
