package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.impl;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLConsoleService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 27.05.15
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SphinxQLConsoleServiceImpl implements SphinxQLConsoleService {

    @Override
    public List<String> getConsoleQueries(String query) {

        List<String> queries = new ArrayList<String>();
        queries.add(SphinxQLService.SET_PROFILING_QUERY);
        queries.add(query);
        queries.add(SphinxQLService.SHOW_PROFILE_QUERY);
        queries.add(SphinxQLService.SHOW_PLAN_QUERY);
        queries.add(SphinxQLService.SHOW_META_QUERY);

        return queries;
    }


}
