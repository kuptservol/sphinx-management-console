package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.DataSource;
import ru.skuptsov.sphinx.console.dao.api.DataSourceDao;

import java.util.List;

@Repository
public class DataSourceDaoImpl extends AbstractCoordinatorHibernateDao<DataSource> implements DataSourceDao {
    @Override
    public List<DataSource> getDataSources() {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        return hbmCriteria.list();
    }

    @Override
    public DataSource findDataSource(String name) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("name", name));
        return (DataSource) hbmCriteria.uniqueResult();
    }
}
