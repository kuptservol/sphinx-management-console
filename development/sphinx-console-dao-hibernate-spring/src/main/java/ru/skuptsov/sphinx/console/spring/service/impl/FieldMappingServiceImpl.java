package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.dao.api.FieldMappingDao;
import ru.skuptsov.sphinx.console.spring.service.api.FieldMappingService;

@Service
public class FieldMappingServiceImpl extends AbstractSpringService<FieldMappingDao, FieldMapping> implements FieldMappingService {

}
