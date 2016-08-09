package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.common.DateDetailing;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.*;
import ru.skuptsov.sphinx.console.dao.api.SearchQueryDao;
import ru.skuptsov.sphinx.console.transformer.SearchQueryGroupedTransformer;
import ru.skuptsov.sphinx.console.transformer.SearchQueryHistoryPointTransformer;

import java.util.List;

@Repository
public class SearchQueryDaoImpl extends AbstractCoordinatorHibernateDao<SearchQuery> implements SearchQueryDao {

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryGrouped> getSearchQueries(SearchQuerySearchParameters searchParameters) {

        int start = searchParameters.getStart() != null ? searchParameters.getStart() : 0;
        int limit = searchParameters.getLimit() != null ? searchParameters.getLimit() : 0;
        String queryString = getQuery(searchParameters) + "\nlimit " + start + ", " + limit;

        SQLQuery query = getSession().createSQLQuery(queryString);
        query.setResultTransformer(new SearchQueryGroupedTransformer());
        addParameters(query, searchParameters);

        return query.list();
    }

    public Long getForCount(SearchQuerySearchParameters searchParameters){
        String queryString = "select count(*) from(" + getQuery(searchParameters) + ") res";
        SQLQuery query = getSession().createSQLQuery(queryString);
        addParameters(query, searchParameters);

        return new Long(query.uniqueResult().toString());
    }

    private String getQuery(SearchQuerySearchParameters searchParameters){
        String queryString = "SELECT sq.search_query_id as id, \n" +
                "sq.query as query, \n" +
                "cr.collection_name as collectionName, \n" +
                "min(sqr.total_time) as totalTimeMin,\n" +
                "max(sqr.total_time) as totalTimeMax,\n" +
                "min(sqr.result_count) as resultCountMin,\n" +
                "max(sqr.result_count) as resultCountMax,\n" +
                "sum(offset != 0) as offsetNotZeroCount,\n" +
                "count(sqr.search_query_id) as searchQueryResultCount\n" +
                "FROM SEARCH_QUERY as sq\n" +
                "inner join SEARCH_QUERY_RESULT sqr on(sq.search_query_id = sqr.search_query_id)\n" +
                "inner join COLLECTION_REPLICA_V cr on(sq.collection_id = cr.collection_id and sqr.replica_id = cr.replica_id)\n" +
                "where (:collection_name is null or cr.collection_name = :collection_name)\n" +
                "and (:server_name is null or :port is null or (cr.server_name=:server_name and cr.listen_port=:port))\n" +
                "and (:date_time_from is null or unix_timestamp(sqr.date_time)*1000 >= :date_time_from)\n" +
                "and (:date_time_to is null or unix_timestamp(sqr.date_time)*1000 <= :date_time_to)\n" +
                "and (:result_count_min is null or sqr.result_count >= :result_count_min)\n" +
                "and (:offset_not_zero is null or sqr.offset > 0)\n" +
                "and (:result_count_zero is null or sqr.result_count = 0)\n" +
                "group by sq.search_query_id, sq.query, sq.collection_id\n" +
                "having (:total_time_min is null or max(sqr.total_time) > :total_time_min)" +
                "and (:result_count_min is null or max(sqr.result_count) > :result_count_min)\n" +
                "and (:offset_not_zero is null or sum(offset != 0) > 0)\n" +
                "and (:result_count_zero is null or count(sqr.search_query_id) > 0)\n";

        return queryString;
    }

    private void addParameters(Query query, SearchQuerySearchParameters searchParameters){
        query.setParameter("collection_name", searchParameters.getCollectionName());
        query.setParameter("server_name", searchParameters.getReplicaName() != null ? searchParameters.getReplicaName().getServerName() : null);
        query.setParameter("port", searchParameters.getReplicaName() != null ? searchParameters.getReplicaName().getPort() : null);
        query.setParameter("date_time_from", searchParameters.getDateFrom() != null ? searchParameters.getDateFrom().getTime() : null);
        query.setParameter("date_time_to", searchParameters.getDateTo() != null ? searchParameters.getDateTo().getTime() : null);
        query.setParameter("total_time_min", searchParameters.getTotalTimeMin());
        query.setParameter("result_count_min", searchParameters.getResultCountMin());
        query.setParameter("offset_not_zero", searchParameters.getOffsetNotZero());
        query.setParameter("result_count_zero", searchParameters.getResultCountZero());
    }

    private void addParameters(Query query, SearchQueryHistorySearchParameters searchParameters){
        query.setParameter("collection_name", searchParameters.getCollectionName());
        query.setParameter("query", searchParameters.getQuery());
        query.setParameter("server_name", searchParameters.getReplicaName() != null ? searchParameters.getReplicaName().getServerName() : null);
        query.setParameter("port", searchParameters.getReplicaName() != null ? searchParameters.getReplicaName().getPort() : null);
        query.setParameter("date_time_from", searchParameters.getDateFrom() != null ? searchParameters.getDateFrom().getTime() : null);
        query.setParameter("date_time_to", searchParameters.getDateTo() != null ? searchParameters.getDateTo().getTime(): null);
    }

    public SearchQuery getSearchQuery(Long collectionId, String query){
        String queryString = "select * from SEARCH_QUERY \n" +
                "where collection_id = :collection_id\n" +
                "and query_crc32 = CRC32(:query)\n" +
                "and query = :query\n" +
                "limit 0,1";

        SQLQuery sqlQuery = getSession().createSQLQuery(queryString);
        sqlQuery.addEntity(SearchQuery.class);
        sqlQuery.setParameter("collection_id", collectionId);
        sqlQuery.setParameter("query", query);

        return (SearchQuery)sqlQuery.uniqueResult();
    }

    private String addLimit(String query, Integer start, Integer limit){
        return query = query + (start != null ? ("limit " + start + ", " + limit + "\n") : "");
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(SearchQueryHistorySearchParameters searchParameters) {

        String queryStr = getQueryHistoryDateDetailedQuery("min(res.total_time), max(res.total_time), avg(res.total_time)", searchParameters.getDateDetailing());
        queryStr = addLimit(queryStr, searchParameters.getStart(), searchParameters.getLimit());
        SQLQuery query = getSession().createSQLQuery(queryStr);
        query.setResultTransformer(new SearchQueryHistoryPointTransformer());
        addParameters(query, searchParameters);

        return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryResultCount(SearchQueryHistorySearchParameters searchParameters) {

        String queryStr = getQueryHistoryDateDetailedQuery("min(res.result_count), max(res.result_count), avg(res.result_count)", searchParameters.getDateDetailing());
        queryStr = addLimit(queryStr, searchParameters.getStart(), searchParameters.getLimit());
        SQLQuery query = getSession().createSQLQuery(queryStr);
        query.setResultTransformer(new SearchQueryHistoryPointTransformer());
        addParameters(query, searchParameters);

        return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(SearchQueryHistorySearchParameters searchParameters) {

        String queryStr = getQueryHistoryDateDetailedQuery("count(res.search_query_result_id)", searchParameters.getDateDetailing());
        queryStr = addLimit(queryStr, searchParameters.getStart(), searchParameters.getLimit());
        SQLQuery query = getSession().createSQLQuery(queryStr);
        query.setResultTransformer(new SearchQueryHistoryPointTransformer());
        addParameters(query, searchParameters);

        return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(SearchQueryHistorySearchParameters searchParameters) {

        String queryStr = getQueryHistoryDateDetailedQuery("sum(res.offset != 0)", searchParameters.getDateDetailing());
        queryStr = addLimit(queryStr, searchParameters.getStart(), searchParameters.getLimit());
        SQLQuery query = getSession().createSQLQuery(queryStr);
        query.setResultTransformer(new SearchQueryHistoryPointTransformer());
        addParameters(query, searchParameters);

        return query.list();
    }

    private String getQueryHistoryResultDetailedQuery(String fieldName){
        String queryString = "select unix_timestamp(sqr.date_time) as dateVal, sqr." + fieldName + " as value\n" +
                "FROM SEARCH_QUERY as sq\n" +
                "inner join SEARCH_QUERY_RESULT sqr on(sq.search_query_id = sqr.search_query_id)\n" +
                "inner join COLLECTION_REPLICA_V cr on(sq.collection_id = cr.collection_id and sqr.replica_id = cr.replica_id)\n" +
                "where (cr.collection_name = :collection_name)\n" +
                "and (sq.query_crc32 = CRC32(:query) and sq.query = :query)\n" +
                "and (:server_name is null or :port is null or (cr.server_name=:server_name and cr.listen_port=:port))\n" +
                "and (:date_time_from is null or unix_timestamp(sqr.date_time)*1000 >= :date_time_from)\n" +
                "and (:date_time_to is null or unix_timestamp(sqr.date_time)*1000 <= :date_time_to)\n" +
                "order by sqr.date_time\n";

        return queryString;

    }

    private String getQueryHistoryDateDetailedQuery(String valueExpression, DateDetailing dateDetailing){
        String dateExpression;
        if(dateDetailing == null) {dateDetailing = DateDetailing.MILLISECOND;}
        switch (dateDetailing){
            case DATE: {
                dateExpression = "res.dateDay";
                break;
            }
            case HOUR: {
                dateExpression = "res.dateDay + INTERVAL res.dateHour HOUR";
                break;
            }
            case HALF_AN_HOUR: {
                dateExpression = "res.dateDay + INTERVAL res.dateHour HOUR + INTERVAL (res.dateHalfHour * 30) MINUTE";
                break;
            }
            case TEN_MINUTES: {
                dateExpression = "res.dateDay + INTERVAL res.dateHour HOUR + INTERVAL (res.dateTenMinutes * 10) MINUTE";
                break;
            }
            case MINUTE: {
                dateExpression = "res.dateDay + INTERVAL res.dateHour HOUR + INTERVAL dateMinute MINUTE";
                break;
            }
            case MILLISECOND: {
                dateExpression = "res.dateVal";
                break;
            }
            default: {
                dateExpression = "res.dateVal";
                break;
            }
        }

        String queryString = "select unix_timestamp(" + dateExpression + ") as dateVal, \n"+
                valueExpression + "\n" +
                "from \n" +
                "(select \n" +
                "sqr.date_time as dateVal, \n" +
                "date(sqr.date_time) as dateDay, \n" +
                "hour(sqr.date_time) as dateHour, \n" +
                "minute(sqr.date_time) >= 30 as dateHalfHour,\n" +
                "minute(sqr.date_time) div 10 as dateTenMinutes,\n" +
                "minute(sqr.date_time) as dateMinute,\n" +
                "sqr.total_time, sqr.search_query_result_id, sqr.result_count, sqr.offset\n" +
                "FROM SEARCH_QUERY as sq\n" +
                "inner join SEARCH_QUERY_RESULT sqr on(sq.search_query_id = sqr.search_query_id)\n" +
                "inner join COLLECTION_REPLICA_V cr on(sq.collection_id = cr.collection_id and sqr.replica_id = cr.replica_id)\n" +
                "where (cr.collection_name = :collection_name)\n" +
                "and (sq.query_crc32 = CRC32(:query) and sq.query = :query)\n" +
                "and (:server_name is null or :port is null or (cr.server_name=:server_name and cr.listen_port=:port))\n" +
                "and (:date_time_from is null or unix_timestamp(sqr.date_time)*1000 >= :date_time_from)\n" +
                "and (:date_time_to is null or unix_timestamp(sqr.date_time)*1000 <= :date_time_to)\n" +
                ") res\n" +
                "group by " + dateExpression + "\n" +
                "order by " + dateExpression + "\n";

        return queryString;

    }

    @Override
    public void deleteAllSearchQueries() {

        String queryString = "delete from SEARCH_QUERY";

        SQLQuery query = getSession().createSQLQuery(queryString);

        query.executeUpdate();
    }

    @Override
    public void deleteOld(Integer queryLogKeepDay) {

        if(queryLogKeepDay != null && queryLogKeepDay > 0){
            //delete old query results
            String queryString = "delete from SEARCH_QUERY_RESULT where DATEDIFF(CURDATE(),date_time) > :queryLogKeepDay";
            SQLQuery query = getSession().createSQLQuery(queryString);
            query.setParameter("queryLogKeepDay", queryLogKeepDay);
            int deletedCount = query.executeUpdate();

            // delete orphan queries
            queryString = "delete FROM SEARCH_QUERY where search_query_id not in(select search_query_id from SEARCH_QUERY_RESULT);";
            query = getSession().createSQLQuery(queryString);
            deletedCount = query.executeUpdate();
        }

    }
}
