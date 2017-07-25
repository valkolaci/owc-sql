package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class InCondition extends Condition {
    private final Field field;
    private final Object[] values;

    public InCondition(
        Field field,
        Object[] values
    ) {
        this.field = field;
        this.values = values;
    }

    @Override
    public String getTemplatedQuery() {
        return field.getTemplatedQuery() + " IN (" + Arrays.stream(values).map(v -> "?").collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public List<Object> getParameters() {
        return Arrays.asList(values);
    }
}
