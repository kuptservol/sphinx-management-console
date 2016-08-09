package ru.skuptsov.sphinx.console.coordinator.model;

public enum IndexFieldType {
	SQL_ATTR_UINT("sql_attr_uint"),
	SQL_ATTR_BOOL("sql_attr_bool"),
	SQL_ATTR_BIGINT("sql_attr_bigint"),
	SQL_ATTR_TIMESTAMP("sql_attr_timestamp"),
	SQL_ATTR_STR2ORDINAL("sql_attr_str2ordinal"),
	SQL_ATTR_FLOAT("sql_attr_float"),
	SQL_ATTR_MULTI("sql_attr_multi"),
	SQL_ATTR_STRING("sql_attr_string"),
	SQL_ATTR_JSON("sql_attr_json"),
	SQL_ATTR_STR2WORDCOUNT("sql_attr_str2wordcount"),
	SQL_FIELD_STRING("sql_field_string"),
	SQL_FIELD_STR2WORDCOUNT("sql_field_str2wordcount"),
	SQL_FILE_FIELD("sql_file_field"),
	SQL_FIELD("sql_field");

	private String title;
	
	IndexFieldType(String title) {
	    this.title = title;	
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public static IndexFieldType getByTitle(String title) {
        IndexFieldType result = null;
        for (IndexFieldType indexFieldType : IndexFieldType.values()) {
            if (indexFieldType.getTitle().equals(title)) {
                return indexFieldType;
            }
        }
        return result;
    }


}
