package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class FieldName extends Field {
    private final String fieldName;

    public FieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getTemplatedQuery() {
        return "`" + fieldName + "`";
    }

    @Override
    public List<Object> getParameters() {
        return Collections.emptyList();
    }
}
