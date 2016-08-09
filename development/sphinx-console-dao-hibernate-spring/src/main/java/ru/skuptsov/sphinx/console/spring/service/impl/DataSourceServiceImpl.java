package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.DataSource;
import ru.skuptsov.sphinx.console.dao.api.DataSourceDao;
import ru.skuptsov.sphinx.console.spring.service.api.DataSourceService;

import java.util.List;


@Service
public class DataSourceServiceImpl extends AbstractSpringService<DataSourceDao, DataSource> implements DataSourceService {
    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getDataSources() {
        return getDao().getDataSources();
    }

    @Override
    @Transactional(readOnly = true)
    public DataSource getDataSource(String name){
        return getDao().findDataSource(name);
    }
}
