package ru.skuptsov.sphinx.console.coordinator.util.impl;

import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.coordinator.util.DeepCopyService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by lnovikova on 3/19/2015.
 */
@Component
public class DeepCopyServiceImpl implements DeepCopyService {

    @Override
    public <T> T deepCopy(Object source, Class<T> T) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
            out.close();

            ObjectInputStream in;
            in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            Object result = in.readObject();
            in.close();
            bos.close();
            return (T) result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Configuration deepCopyConfiguration(Configuration configuration){

        Configuration newSearchConfiguration = deepCopy(configuration, Configuration.class);
        newSearchConfiguration.setId(null);
        newSearchConfiguration.setConfigurationTemplate(configuration.getConfigurationTemplate());
        newSearchConfiguration.setIndexerConfigurationTemplate(configuration.getIndexerConfigurationTemplate());
        newSearchConfiguration.setSearchConfigurationTemplate(configuration.getSearchConfigurationTemplate());
        newSearchConfiguration.setDatasource(configuration.getDatasource());
        for(FieldMapping fieldMapping : newSearchConfiguration.getFieldMappings()){
            fieldMapping.setId(null);
            fieldMapping.setConfiguration(newSearchConfiguration);
        }
        for(ConfigurationFields configurationFields : newSearchConfiguration.getSourceConfigurationFields()){
            configurationFields.setId(null);
            configurationFields.setConfiguration(newSearchConfiguration);
        }
        newSearchConfiguration.setSearchConfigurationFields(null);
        return newSearchConfiguration;
    }
}
