package ru.skuptsov.sphinx.console.coordinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FieldMapping extends BaseEntity {
	private Long id;
	@NotEmpty
    private String sourceField;
    private String sourceFieldType;
	@NotEmpty
    private String indexField;
	@NotNull
    private IndexFieldType indexFieldType;
    private String indexFieldCommentary;
	@NotNull
    private Boolean isId;
	@Valid
    @JsonIgnore
    private Configuration configuration;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getIndexField() {
        return indexField;
    }

    public void setIndexField(String indexField) {
        this.indexField = indexField;
    }

    public IndexFieldType getIndexFieldType() {
		return indexFieldType;
	}

	public void setIndexFieldType(IndexFieldType indexFieldType) {
		this.indexFieldType = indexFieldType;
	}

	public String getIndexFieldCommentary() {
        return indexFieldCommentary;
    }

    public void setIndexFieldCommentary(String indexFieldCommentary) {
        this.indexFieldCommentary = indexFieldCommentary;
    }

    public Boolean getIsId() {
        return isId;
    }

    public void setIsId(Boolean isId) {
        this.isId = isId;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

	public String getSourceFieldType() {
		return sourceFieldType;
	}

	public void setSourceFieldType(String sourceFieldType) {
		this.sourceFieldType = sourceFieldType;
	}
    
	@Override  
	public int hashCode() {  
	    return new HashCodeBuilder()  
	         .append(this.sourceFieldType)  
	         .append(this.sourceField)
	         .append(this.indexField)
	         .append(this.indexFieldType)
	         .append(this.isId)
	         .toHashCode();  
    }
	
	@Override
	public boolean equals(Object other) {
	      if (this == other) { return true; }
	      if ((other == null) || (other.getClass() != this.getClass())) { return false; }

	      FieldMapping castOther = (FieldMapping) other;
	      return new EqualsBuilder()
	    		  .append(this.indexField, castOther.getIndexField())
	    		  .append(this.indexFieldType, castOther.getIndexFieldType())
	    		  .append(this.sourceField, castOther.getSourceField())
                  .append(this.sourceFieldType == null ? "" : this.sourceFieldType, castOther.getSourceFieldType() == null ? "" : castOther.getSourceFieldType())
                  .append(this.isId, castOther.getIsId())
	    		  .isEquals();
	}

}
