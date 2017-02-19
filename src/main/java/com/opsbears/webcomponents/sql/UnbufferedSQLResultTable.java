package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UnbufferedSQLResultTable extends SQLResultTable<UnbufferedSQLResultColumn> {
    SQLResultRow<UnbufferedSQLResultColumn> fetchNextRow() throws IndexOutOfBoundsException;
}
