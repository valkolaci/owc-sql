package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;

@ParametersAreNonnullByDefault
abstract class UnbufferedResultTable implements UnbufferedSQLResultTable {
    protected final Map<String, UnbufferedResultColumn> columns;

    UnbufferedResultTable(Map<String, UnbufferedResultColumn> columns) {
        this.columns = Collections.unmodifiableMap(columns);
    }

    @Override
    public UnbufferedSQLResultColumn getColumnByName(String columnName) {
        return this.columns.get(columnName);
    }

}
