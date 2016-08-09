package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.api.EntityDao;

@Repository
public class EntityDaoImpl extends AbstractCoordinatorHibernateDao<BaseEntity> implements EntityDao {

    @Override
    public boolean existsById(Long id, Class clazz) {
        BaseEntity res = (BaseEntity)(getSession().get(clazz, id));
        return res != null;
    }

    @Override
    public boolean existsByField(Class clazz, String fieldName, Object fieldValue) {
        boolean exists = false;

        Criteria hbmCriteria = getSession().createCriteria(clazz);
        hbmCriteria.add(Restrictions.eq(fieldName, fieldValue));
        exists = hbmCriteria.list() != null && hbmCriteria.list().size() > 0;

        return exists;
    }
}
