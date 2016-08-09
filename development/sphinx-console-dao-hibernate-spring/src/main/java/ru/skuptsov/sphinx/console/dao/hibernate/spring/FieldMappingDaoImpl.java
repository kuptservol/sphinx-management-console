package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.springframework.stereotype.Repository;

import ru.skuptsov.sphinx.console.coordinator.model.FieldMapping;
import ru.skuptsov.sphinx.console.dao.api.FieldMappingDao;

@Repository
public class FieldMappingDaoImpl extends AbstractCoordinatorHibernateDao<FieldMapping> implements FieldMappingDao {

}
