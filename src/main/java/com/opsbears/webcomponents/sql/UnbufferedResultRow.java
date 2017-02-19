package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
class UnbufferedResultRow extends ResultRow<UnbufferedSQLResultColumn> implements UnbufferedSQLResultRow {
    UnbufferedResultRow(Map<String, SQLResultField<UnbufferedSQLResultColumn>> fields) {
        super(fields);
    }
}
