package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.dao.api.AdminProcessDao;

import java.util.List;

@Repository
public class AdminProcessDaoImpl extends AbstractCoordinatorHibernateDao<AdminProcess> implements AdminProcessDao {
    private static final String DELETE_ADMIN_PROCESS_BY_SERVER_QUERY = "delete from sphinx.console.ADMIN_PROCESS where SERVER_ID = :server_id";
    private static final String DELETE_ADMIN_PROCESS_BY_SERVER_NAME_QUERY =
            "delete from sphinx.console.ADMIN_PROCESS where SERVER_ID in (select distinct SERVER_ID from sphinx.console.SERVER where NAME = :server_name)";

	@Override
	public List<AdminProcess> getAdminProcesses(Long serverId) {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("server", "server", JoinType.INNER_JOIN);
        if(serverId!=null){
            hbmCriteria.add(Restrictions.eq("server.id", serverId));
        }

		return hbmCriteria.list();
	}

    @Override
    public List<AdminProcess> getAdminProcesses(String serverName) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("server", "server", JoinType.INNER_JOIN);
        if(serverName != null && !"".equals(serverName)) {
            hbmCriteria.add(Restrictions.eq("server.name", serverName));
        }

        return hbmCriteria.list();
    }

    @Override
    public AdminProcess getAdminProcess(ProcessType processType, String serverName) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("server", "server", JoinType.INNER_JOIN);
        hbmCriteria.add(Restrictions.eq("type", processType));
        if (serverName != null) {
            hbmCriteria.add(Restrictions.eq("server.name", serverName));
        }
        return (AdminProcess) hbmCriteria.uniqueResult();
    }

    @Override
    public void deleteAdminProcessesByServer(Long serverId) {
        SQLQuery query = getSession().createSQLQuery(DELETE_ADMIN_PROCESS_BY_SERVER_QUERY);
        query.setParameter("server_id", serverId);
        query.executeUpdate();
    }

    @Override
    public void deleteAdminProcessesByServer(String serverName) {
        SQLQuery query = getSession().createSQLQuery(DELETE_ADMIN_PROCESS_BY_SERVER_NAME_QUERY);
        query.setParameter("server_name", serverName);
        query.executeUpdate();
    }
}
