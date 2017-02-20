package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class UnbufferedResultField extends ResultField<UnbufferedSQLResultColumn> implements UnbufferedSQLResultField {
    UnbufferedResultField(String name, @Nullable Object value) {
        super(name, value);
    }
}
