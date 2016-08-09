package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.dao.api.ConfigurationFieldsDao;
import ru.skuptsov.sphinx.console.dao.common.hibernate.spring.util.HibernateInitializer;

@Repository
public class ConfigurationFieldsDaoImpl extends AbstractCoordinatorHibernateDao<ConfigurationFields> implements ConfigurationFieldsDao {
    @Override
    public ConfigurationFields getSearchPort(Long configurationId){
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        
        hbmCriteria.add(Restrictions.eq("configuration.id", configurationId));
        hbmCriteria.add(Restrictions.eq("fieldKey", "listen"));
        
        ConfigurationFields configurationField = (ConfigurationFields) hbmCriteria.uniqueResult();
        HibernateInitializer.initializeProperties(configurationField, "configuration");
        return configurationField;
    }
    
    @Override
    public ConfigurationFields getDistributedPort(Long configurationId){
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        
        hbmCriteria.add(Restrictions.eq("configuration.id", configurationId));
        hbmCriteria.add(Restrictions.eq("fieldKey", "distributed_listen"));
        
        ConfigurationFields configurationField = (ConfigurationFields) hbmCriteria.uniqueResult();
        HibernateInitializer.initializeProperties(configurationField, "configuration");
        return configurationField;
    }
}
