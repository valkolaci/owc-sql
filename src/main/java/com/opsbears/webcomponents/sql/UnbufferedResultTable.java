package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;

@ParametersAreNonnullByDefault
abstract class UnbufferedResultTable implements UnbufferedSQLResultTable {
    protected final Map<String, UnbufferedSQLResultColumn> columns;

    UnbufferedResultTable(Map<String, UnbufferedSQLResultColumn> columns) {
        this.columns = Collections.unmodifiableMap(columns);
    }

    public Map<String, UnbufferedSQLResultColumn> getColumns() {
        return columns;
    }

    @Override
    public UnbufferedSQLResultColumn getColumnByName(String columnName) {
        return this.columns.get(columnName);
    }

}
