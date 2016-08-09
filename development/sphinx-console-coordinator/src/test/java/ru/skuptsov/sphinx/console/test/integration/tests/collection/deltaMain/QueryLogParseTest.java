package ru.skuptsov.sphinx.console.test.integration.tests.collection.deltaMain;

import org.junit.Assert;
import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaName;
import ru.skuptsov.sphinx.console.coordinator.model.common.DateDetailing;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.*;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentDeltaMainCollectionHelper;

import java.util.Date;
import java.util.List;

public class QueryLogParseTest extends TestEnvironmentDeltaMainCollectionHelper {

    @Test
    public void searchQueryTest() throws Throwable {

/*
         на момент выполнения теста должны быть пропаршенные логи. частота парсинга зависит от параметра
         "query.log.parse.delay" и на данный момент составляет 5 минут, этого хватает, чтобы были какие-то
         результаты парсинга на момент выполнения теста

         тест один на поиск запросов и поиск истории, т.к. анализируется один целевой запрос
*/

        //
        // проверка фильтра на вкладке "Анализ запросов"
        //
        SearchQuerySearchParameters searchParameters = new SearchQuerySearchParameters();
        searchParameters.setStart(0);
        searchParameters.setLimit(10);
        // пустые параметры поиска
        List<SearchQueryGrouped> list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterEmptyCount = list.size();
        Assert.assertTrue(filterEmptyCount > 0);

        // поле "Коллекция"
        searchParameters.setCollectionName(deltaMainCollectionName);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterCollectionCount = list.size();
        Assert.assertTrue(filterCollectionCount > 0);

        // поле "Реплика"
        ReplicaName replicaName = new ReplicaName();
        replicaName.setServerName(searchAgentServer.getName());
        replicaName.setPort(replica2SearchPort);
        searchParameters.setReplicaName(replicaName);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterReplicaCount = list.size();
        Assert.assertTrue(filterReplicaCount > 0 && filterReplicaCount < filterCollectionCount);

        // поле "Дольше"
        SearchQueryGrouped firstQuery = list.get(0);
        searchParameters.setTotalTimeMin(firstQuery.getTotalTimeMax() + 1);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterTotalTimeCount = list.size();
        Assert.assertTrue(filterTotalTimeCount < filterReplicaCount);
        searchParameters.setTotalTimeMin(null);

        // поле "Больше"
        searchParameters.setResultCountMin(firstQuery.getResultCountMax() + 1);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterResultCountCount = list.size();
        Assert.assertTrue(filterResultCountCount < filterReplicaCount);
        searchParameters.setResultCountMin(null);

        // поле "Дальше 1-ой страницы"
        searchParameters.setOffsetNotZero(true);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterOffsetNotZeroCount = list.size();
        Assert.assertTrue(filterResultCountCount < filterReplicaCount);
        searchParameters.setOffsetNotZero(null);

        // поле "Без результатов"
        searchParameters.setResultCountZero(true);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        int filterResultCountZeroCount = list.size();
        Assert.assertTrue(filterResultCountZeroCount < filterReplicaCount);
        searchParameters.setResultCountZero(null);

        // поле "Дата и время выполнения"
        searchParameters.setReplicaName(null);
        searchParameters.setResultCountMin(2);
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        firstQuery = list.get(0);
        int firstQueryQueryCount = firstQuery.getSearchQueryResultCount();
        SearchQueryHistorySearchParameters searchParametersHistory = new SearchQueryHistorySearchParameters();
        searchParametersHistory.setCollectionName(firstQuery.getCollectionName());
        searchParametersHistory.setQuery(firstQuery.getQuery());
        searchParametersHistory.setStart(0);
        searchParametersHistory.setLimit(10);
        List<SearchQueryHistoryPoint> listHistory = testExecutor.getQueryHistoryTotalTime(searchParametersHistory);
        // "c"
        long dateTimestampFirst = listHistory.get(0).getDate();
        searchParameters.setDateFrom(new Date(dateTimestampFirst + 1));
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        firstQuery = list.get(0);
        int firstQueryQueryWithStartDateFilterCount = firstQuery.getSearchQueryResultCount();
        Assert.assertTrue(firstQueryQueryWithStartDateFilterCount == firstQueryQueryCount - 1);
        searchParameters.setDateFrom(null);
        // "по"
        long dateTimestampSecond = listHistory.get(1).getDate();
        searchParameters.setDateTo(new Date(dateTimestampSecond - 1));
        list = testExecutor.getSearchQueriesResultsGrouped(searchParameters);
        firstQuery = list.get(0);
        int firstQueryQueryWithEndDateFilterCount = firstQuery.getSearchQueryResultCount();
        Assert.assertTrue(firstQueryQueryWithEndDateFilterCount == 1);

        //
        // проверка фильтра в окне "История запроса"
        //

        // пустой фильтр
        listHistory = testExecutor.getQueryHistoryTotalTime(searchParametersHistory);
        Assert.assertTrue(listHistory.size() > 0);

        // поле "Реплика"
        searchParametersHistory.setReplicaName(replicaName);
        listHistory = testExecutor.getQueryHistoryTotalTime(searchParametersHistory);
        Assert.assertTrue(listHistory.size() > 0);
        searchParametersHistory.setReplicaName(null);

        // поле "Дата и время выполнения"
        listHistory = testExecutor.getQueryHistoryTotalTime(searchParametersHistory);
        int listHistoryEmptyFilter = listHistory.size();
        dateTimestampFirst = listHistory.get(0).getDate();
        dateTimestampSecond = listHistory.get(1).getDate();
        // c
        searchParametersHistory.setDateFrom(new Date(dateTimestampFirst + 1));
        listHistory = testExecutor.getQueryHistoryTotalTime(searchParametersHistory);
        Assert.assertTrue(listHistory.size() == listHistoryEmptyFilter - 1);
        searchParametersHistory.setDateFrom(null);
        // по
        searchParametersHistory.setDateTo(new Date(dateTimestampSecond - 1));
        listHistory = testExecutor.getQueryHistoryTotalTime(searchParametersHistory);
        Assert.assertTrue(listHistory.size() == 1);
        searchParametersHistory.setDateTo(null);

        // закладка "Кол-во запросов"
        listHistory = testExecutor.getQueryHistoryQueryCount(searchParametersHistory);
        int sizeWithoutGrouping = listHistory.size();
        Assert.assertTrue(sizeWithoutGrouping > 0);
        // проверка группировки (уровня детализации)
        searchParametersHistory.setDateDetailing(DateDetailing.MINUTE);
        listHistory = testExecutor.getQueryHistoryQueryCount(searchParametersHistory);
        Assert.assertTrue(listHistory.size() > 0 && listHistory.size() < sizeWithoutGrouping);

        // закладка "Кол-во результатов"
        listHistory = testExecutor.getQueryHistoryResultCount(searchParametersHistory);
        Assert.assertTrue(listHistory.size() > 0);

        // закладка "Переходы дальше 1-ой страницы"
        searchParametersHistory.setDateDetailing(DateDetailing.MILLISECOND);
        listHistory = testExecutor.getQueryHistoryOffsetNotZeroCount(searchParametersHistory);
        sizeWithoutGrouping = listHistory.size();
        Assert.assertTrue(sizeWithoutGrouping > 0);
        // проверка группировки (уровня детализации)
        searchParametersHistory.setDateDetailing(DateDetailing.MINUTE);
        listHistory = testExecutor.getQueryHistoryOffsetNotZeroCount(searchParametersHistory);
        Assert.assertTrue(listHistory.size() > 0 && listHistory.size() < sizeWithoutGrouping);
    }

}
