package ru.skuptsov.sphinx.console.admin.dao.api;

import ru.skuptsov.sphinx.console.dao.common.api.Dao;
import ru.skuptsov.sphinx.console.admin.model.Command2Save;

public interface CommandDao extends Dao<Command2Save> {
    public boolean isExist(String fullId);
    public void deleteByName(String fullId);
}
