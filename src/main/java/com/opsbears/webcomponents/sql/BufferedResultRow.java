package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
class BufferedResultRow extends ResultRow<BufferedSQLResultColumn> implements BufferedSQLResultRow {
    BufferedResultRow(Map<String, SQLResultField<BufferedSQLResultColumn>> fields) {
        super(fields);
        for (SQLResultField<BufferedSQLResultColumn> field : fields.values()) {
            if (field instanceof BufferedResultField) {
                ((BufferedResultField)field).linkRow(this);
            }
        }
    }
}
