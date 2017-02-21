package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface SQLResultTable<TColumnType extends SQLResultColumn> {
    Map<String, TColumnType> getColumns();
    TColumnType getColumnByName(String columnName);
}
