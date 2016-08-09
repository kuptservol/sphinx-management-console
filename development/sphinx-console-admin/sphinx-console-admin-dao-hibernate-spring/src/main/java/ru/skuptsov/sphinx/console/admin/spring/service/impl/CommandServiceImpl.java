package ru.skuptsov.sphinx.console.admin.spring.service.impl;

import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.admin.dao.api.CommandDao;
import ru.skuptsov.sphinx.console.admin.model.Command2Save;
import ru.skuptsov.sphinx.console.admin.spring.service.api.CommandService;

import ru.skuptsov.sphinx.console.spring.service.impl.AbstractSpringService;


@Service
public class CommandServiceImpl extends AbstractSpringService<CommandDao, Command2Save> implements CommandService {
    @Override
    public boolean isExist(String fullId) {
        return getDao().isExist(fullId);
    }

    @Override
    public void deleteByName(String fullId) {
        getDao().deleteByName(fullId);
    }
}
