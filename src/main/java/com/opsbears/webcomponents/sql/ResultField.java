package com.opsbears.webcomponents.sql;

import java.util.Objects;

abstract class ResultField<TColumnType extends SQLResultColumn> implements SQLResultField<TColumnType> {
    private String name;
    private Object value;
    private ResultRow<TColumnType> row;
    private TColumnType column;

    ResultField(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public TColumnType getColumn() {
        Objects.requireNonNull(column, "BUG: column linkage is missing");
        return column;
    }

    @Override
    public SQLResultRow<TColumnType> getRow() {
        Objects.requireNonNull(row, "BUG: row linkage is missing");
        return row;
    }

    void linkRow(ResultRow<TColumnType> row) {
        this.row = row;
    }

    void linkColumn(TColumnType column) {
        this.column = column;
    }


}
