package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface BufferedSQLResultTable extends
    SQLResultTable<BufferedSQLResultColumn>,
    Iterable<SQLResultRow<BufferedSQLResultColumn>>,
    List<SQLResultRow<BufferedSQLResultColumn>> {

    SQLResultRow<BufferedSQLResultColumn> getRow(int row);
}
