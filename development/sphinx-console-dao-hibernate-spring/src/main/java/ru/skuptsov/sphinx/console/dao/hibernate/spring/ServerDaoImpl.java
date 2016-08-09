package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.dao.api.ServerDao;

import java.util.List;

@Repository
public class ServerDaoImpl extends AbstractCoordinatorHibernateDao<Server> implements ServerDao {

	@Override
	public List<Server> getServers() {
		
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		
		return hbmCriteria.list();
	}

    @Override
    public Server getServer(String name) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("name", name));
        return (Server) hbmCriteria.uniqueResult();
    }

    @Override
    public List<Server> getServers(SphinxProcessType sphinxProcessType) {
        Criteria hbmCriteria = getSession().createCriteria(SphinxProcess.class);
        hbmCriteria.createAlias("server", "server", JoinType.INNER_JOIN);
        if(sphinxProcessType != null) {
            hbmCriteria.add(Restrictions.eq("type", sphinxProcessType));
        }
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("server"));
        hbmCriteria.setProjection(Projections.distinct(proList));
        hbmCriteria.addOrder(Order.asc("server.name"));

        return hbmCriteria.list();
    }
	
}
