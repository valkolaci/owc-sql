package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class Parameter extends Field {
    private final Object value;

    public Parameter(@Nullable Object value) {
        this.value = value;
    }

    @Override
    public String getTemplatedQuery() {
        return " ? ";
    }

    @Override
    public List<Object> getParameters() {
        return Collections.singletonList(value);
    }
}
