package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ru.skuptsov.sphinx.console.coordinator.model.CollectionRoleType;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationTemplate;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationType;
import ru.skuptsov.sphinx.console.dao.api.ConfigurationTemplateDao;

import java.util.List;

@Repository
public class ConfigurationTemplateDaoImpl extends AbstractCoordinatorHibernateDao<ConfigurationTemplate> implements ConfigurationTemplateDao {

	@Override
	public List<ConfigurationTemplate> getConfigurationTemplates() {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("type", ConfigurationType.CONFIGURATION));
		return hbmCriteria.list();
	}

    @Override
    public List<ConfigurationTemplate> getSearchConfigurationTemplates() {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("type", ConfigurationType.SEARCH));
        return hbmCriteria.list();
    }

    @Override
    public List<ConfigurationTemplate> getIndexConfigurationTemplates() {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("type", ConfigurationType.INDEX));
        return hbmCriteria.list();
    }

    @Override
    public void addConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        add(configurationTemplate);
    }

    @Override
    public void updateConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        saveOrUpdate(configurationTemplate);
    }

    @Override
    public void deleteConfigurationTemplate(Long id) {
        deleteById(id);
    }

    @Override
    public ConfigurationTemplate getConfigurationTemplate(String name) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("name", name));
        return (ConfigurationTemplate) hbmCriteria.uniqueResult();
    }

    @Override
    public ConfigurationTemplate getConfigurationTemplate(String name, ConfigurationType configurationType, CollectionRoleType collectionType) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("name", name)).add(Restrictions.eq("type", configurationType)).
        		add(Restrictions.eq("collectionType", collectionType));
        return (ConfigurationTemplate) hbmCriteria.uniqueResult();
    }
}
