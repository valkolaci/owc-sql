package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class BufferedResultField implements BufferedSQLResultField {
    private String name;
    @Nullable
    private Object value;
    private BufferedSQLResultColumn column;
    private BufferedSQLResultRow row;

    BufferedResultField(String name, @Nullable Object value) {
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

    void linkColumn(BufferedSQLResultColumn column) {
        this.column = column;
    }

    @Override
    public BufferedSQLResultColumn getColumn() {
        return column;
    }

    void linkRow(BufferedSQLResultRow row) {
        this.row = row;
    }

    @Override
    public BufferedSQLResultRow getRow() {
        return row;
    }
}
