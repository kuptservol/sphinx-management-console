package ru.skuptsov.sphinx.console.coordinator.util;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;

/**
 * Created by lnovikova on 3/19/2015.
 */
public interface DeepCopyService {
    <T> T deepCopy(Object source, Class<T> T);
    Configuration deepCopyConfiguration(Configuration configuration);
}
