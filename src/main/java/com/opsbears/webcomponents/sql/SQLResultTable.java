package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SQLResultTable<TColumnType extends SQLResultColumn> {
    TColumnType getColumnByName(String columnName);
}
