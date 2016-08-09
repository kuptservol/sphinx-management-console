package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryResult;
import ru.skuptsov.sphinx.console.dao.api.SearchQueryResultDao;

import java.util.Date;

@Repository
public class SearchQueryResultDaoImpl extends AbstractCoordinatorHibernateDao<SearchQueryResult> implements SearchQueryResultDao {

    @Override
    public Date getLastParseDate(String collectionName, Long replicaNumber) {
        String queryString = "SELECT max(date_time) FROM SEARCH_QUERY_RESULT sqr\n" +
        "inner join SEARCH_QUERY sq on(sqr.search_query_id = sq.search_query_id)\n" +
        "inner join COLLECTION_REPLICA_V cr on(sq.collection_id = cr.collection_id and sqr.replica_id = cr.replica_id)\n" +
        "where cr.collection_name = :collection_name\n" +
        "and cr.replica_number = :replica_number";

        SQLQuery query = getSession().createSQLQuery(queryString);
        query.setParameter("collection_name", collectionName);
        query.setParameter("replica_number", replicaNumber);

        return (Date)query.uniqueResult();
    }
}
