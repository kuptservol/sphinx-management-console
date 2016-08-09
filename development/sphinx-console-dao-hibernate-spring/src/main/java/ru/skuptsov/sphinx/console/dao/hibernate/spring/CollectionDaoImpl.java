package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.params.CollectionSearchParameters;
import ru.skuptsov.sphinx.console.dao.api.CollectionDao;

import java.util.List;

@Repository
public class CollectionDaoImpl extends AbstractCoordinatorHibernateDao<Collection> implements CollectionDao {
	
	private static final String CLEAR_LOGS_QUERY = "delete from sphinx.console.ACTIVITY_LOG where COLLECTION_ID = :collection_id";

	@Override
	public List<Collection> getCollections() {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		return hbmCriteria.list();
	}

    @Override
    public List<Collection> getCollections(CollectionSearchParameters searchParameters) {
        String queryString = "select * from COLLECTION c \n " +
                "left outer join SPHINX_PROCESS ip on ip.collection_id = c.collection_id and ip.type='INDEXING' left outer join SERVER isr on isr.server_id = ip.server_id ";
        boolean needWhere = true;
        if(StringUtils.isNotEmpty(searchParameters.getName())) {
            queryString += "where c.collection_name like '%" + searchParameters.getName() +"%' ";
            needWhere = false;
        }
        if(StringUtils.isNotEmpty(searchParameters.getSearchServerName())) {
            queryString += (needWhere ? "where" : " and") + " ss.name = '" + searchParameters.getSearchServerName() + "'";
            needWhere = false;
        }
        if(StringUtils.isNotEmpty(searchParameters.getIndexServerName())) {
            queryString += (needWhere ? "where" : " and") + " isr.name = '" + searchParameters.getIndexServerName() + "'";
        }
        queryString += " order by c.collection_name";

        SQLQuery query = getSession().createSQLQuery(queryString);
        query.addEntity(Collection.class);

        if(searchParameters.getPagingEnabled() && searchParameters.getPage()!= null && searchParameters.getLimit() != null) {
            query.setFirstResult((searchParameters.getPage() - 1) * searchParameters.getLimit())
                    .setMaxResults(searchParameters.getLimit());
        }

        return query.list();
    }

    @Override
    public Collection getCollection(String name) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("name", name));
        return (Collection) hbmCriteria.uniqueResult();
    }

    @Override
    public Collection getCollection(Long id) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("id", id));
        return (Collection) hbmCriteria.uniqueResult();
    }

	@Override
	public void clearLogs(Long collectionId) {
		SQLQuery query = getSession().createSQLQuery(CLEAR_LOGS_QUERY);
		query.setParameter("collection_id", collectionId);
    	query.executeUpdate();	
	}

    @Override
    public List<Collection> getNameCollectionsByTemplateId(Long id) {
        String queryString = "select distinct * from COLLECTION c \n" +
                "left outer join SPHINX_PROCESS sp on sp.collection_id = c.collection_id and sp.type = 'SEARCHING' inner join CONFIGURATION sconf on sconf.configuration_id = sp.configuration_id inner join CONFIGURATION_TEMPLATE stempl on (sconf.searchd_configuration_template_id = stempl.configuration_template_id or sconf.configuration_template_id = stempl.configuration_template_id or sconf.indexer_configuration_template_id = stempl.configuration_template_id) \n" +
                "left outer join SPHINX_PROCESS ip on ip.collection_id = c.collection_id and ip.type='INDEXING' inner join CONFIGURATION iconf on iconf.configuration_id = ip.configuration_id inner join CONFIGURATION_TEMPLATE itempl on (sconf.searchd_configuration_template_id = stempl.configuration_template_id or sconf.configuration_template_id = stempl.configuration_template_id or sconf.indexer_configuration_template_id = stempl.configuration_template_id)";

        queryString += "where stempl.configuration_template_id="+id+" and itempl.configuration_template_id="+id;

        SQLQuery query = getSession().createSQLQuery(queryString);


        query.addEntity(Collection.class);

        return query.list();
    }

	@Override
	public List<Collection> getFailureFalseCollections() {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		hbmCriteria.add(Restrictions.eq("isProcessingFailed", false));
		return hbmCriteria.list();
	}

	@Override
	public List<Collection> getSimpleCollections() {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		hbmCriteria.createAlias("sphinxProcesses", "sphinxProcesses", JoinType.LEFT_OUTER_JOIN);
		hbmCriteria.createAlias("sphinxProcesses.server", "server", JoinType.LEFT_OUTER_JOIN);
		hbmCriteria.createAlias("sphinxProcesses.configuration", "configuration", JoinType.LEFT_OUTER_JOIN);
		hbmCriteria.createAlias("configuration.searchConfigurationFields", "configurationField", JoinType.LEFT_OUTER_JOIN);
		
		hbmCriteria.add(Restrictions.eq("collectionType", CollectionRoleType.SIMPLE));
		hbmCriteria.add(Restrictions.eq("sphinxProcesses.type", SphinxProcessType.SEARCHING));
		hbmCriteria.add(Restrictions.eq("configurationField.fieldKey", "distributed_listen"));
		
		hbmCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return hbmCriteria.list();
	}

    @Override
    public List<Collection> getDistributedCollections(String collectionName) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("collectionType", CollectionRoleType.DISTRIBUTED));

        if(StringUtils.isNotEmpty(collectionName)){
            hbmCriteria.createAlias("distributedCollectionNodes", "distributedCollectionNodes", JoinType.INNER_JOIN);
            hbmCriteria.createAlias("distributedCollectionNodes.simpleCollection", "simpleCollection", JoinType.INNER_JOIN);
            hbmCriteria.add(Restrictions.eq("simpleCollection.name", collectionName));
        }

        hbmCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return hbmCriteria.list();
    }

	@Override
	public List<Collection> getCollectionsForSnippetCreation() {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		hbmCriteria.createAlias("snippetConfiguration", "snippet", JoinType.LEFT_OUTER_JOIN);
		hbmCriteria.createAlias("sphinxProcesses", "sphinxProcesses", JoinType.LEFT_OUTER_JOIN);
		hbmCriteria.createAlias("sphinxProcesses.configuration", "configuration", JoinType.LEFT_OUTER_JOIN);
		hbmCriteria.createAlias("configuration.fieldMappings", "fieldMapping", JoinType.INNER_JOIN);
		
		hbmCriteria.add(Restrictions.isNull("snippet.id"));
		hbmCriteria.add(Restrictions.eq("collectionType", CollectionRoleType.SIMPLE));
		hbmCriteria.add(Restrictions.eq("sphinxProcesses.type", SphinxProcessType.SEARCHING));
		hbmCriteria.add(Restrictions.eq("fieldMapping.indexFieldType", IndexFieldType.SQL_FIELD));
		
		
		hbmCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return hbmCriteria.list();
	}
}
