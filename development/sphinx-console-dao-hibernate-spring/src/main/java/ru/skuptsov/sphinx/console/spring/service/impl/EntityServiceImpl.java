package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.api.EntityDao;
import ru.skuptsov.sphinx.console.spring.service.api.EntityService;

@Service
public class EntityServiceImpl extends AbstractSpringService<EntityDao, BaseEntity> implements EntityService {

	@Override
	@Transactional(readOnly = true)
	public boolean existsById(Long id, Class clazz) {
			return getDao().existsById(id, clazz);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByField(Class clazz, String fieldName, Object fieldValue) {
		return getDao().existsByField(clazz, fieldName, fieldValue);
	}

}
