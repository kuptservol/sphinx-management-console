package ru.skuptsov.sphinx.console.sphinx.service.api;

public interface SphinxServiceStrategyFactory {
    SphinxService getSphinxService(String type);
}
