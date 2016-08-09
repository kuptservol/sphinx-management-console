package ru.skuptsov.sphinx.console.admin.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.admin.dao.api.CommandDao;
import ru.skuptsov.sphinx.console.admin.model.Command2Save;

@Repository
public class CommandDaoImpl extends AbstractCoordinatorHibernateDao<Command2Save> implements CommandDao {
    private static final String DELETE_BY_COMMAND_ID = "delete from sphinx.console.COMMAND where COMMAND_ID = :command_id";

    @Override
    @Transactional(readOnly = true)
    public boolean isExist(String fullId) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());

        hbmCriteria.add(Restrictions.eq("commandId", fullId));

        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.count("id"));
        hbmCriteria.setProjection(Projections.distinct(proList));

        return hbmCriteria.uniqueResult() != null ? ((Long)hbmCriteria.uniqueResult()) > 0 : false;
    }

    @Override
    @Transactional
    public void deleteByName(String fullId) {
        SQLQuery query = getSession().createSQLQuery(DELETE_BY_COMMAND_ID);
        query.setParameter("command_id", fullId);
        query.executeUpdate();
    }
}
