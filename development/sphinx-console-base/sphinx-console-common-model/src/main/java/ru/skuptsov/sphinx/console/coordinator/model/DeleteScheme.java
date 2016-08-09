package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.constraints.NotNull;

public class DeleteScheme extends BaseEntity {
	private Long id;
    @NotNull
    private DeleteSchemeType type;
    @NotEmpty
    private String fieldKey;
    @NotEmpty
    private String fieldValueFrom;
    @NotEmpty
    private String fieldValueTo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeleteSchemeType getType() {
        return type;
    }

    public void setType(DeleteSchemeType type) {
        this.type = type;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldValueFrom() {
        return fieldValueFrom;
    }

    public void setFieldValueFrom(String fieldValueFrom) {
        this.fieldValueFrom = fieldValueFrom;
    }

    public String getFieldValueTo() {
        return fieldValueTo;
    }

    public void setFieldValueTo(String fieldValueTo) {
        this.fieldValueTo = fieldValueTo;
    }
}
