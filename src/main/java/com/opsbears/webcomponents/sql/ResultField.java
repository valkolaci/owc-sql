package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import java.util.Objects;

abstract class ResultField<TColumnType extends SQLResultColumn> implements SQLResultField<TColumnType> {
    private String name;
    @Nullable
    private Object value;
    private ResultRow<TColumnType> row;
    private TColumnType column;

    ResultField(String name, @Nullable Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @Nullable
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
