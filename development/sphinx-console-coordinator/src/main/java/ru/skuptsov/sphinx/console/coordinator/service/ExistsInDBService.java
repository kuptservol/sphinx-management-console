package ru.skuptsov.sphinx.console.coordinator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.spring.service.api.EntityService;

/**
 * Created by lnovikova on 3/16/2015.
 */
@Component
public class ExistsInDBService{

    @Autowired
    private EntityService entityService;


    public boolean existsInDB(BaseEntity baseEntity, Class clazz){
        return entityService.existsById(baseEntity.getId(), clazz);
    }

}
