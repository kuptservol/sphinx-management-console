package ru.skuptsov.sphinx.console.admin.spring.service.api;

import ru.skuptsov.sphinx.console.admin.model.Command2Save;
import ru.skuptsov.sphinx.console.spring.service.api.Service;


public interface CommandService extends Service<Command2Save> {
    public boolean isExist(String fullId);
    public void deleteByName(String fullId);
}
