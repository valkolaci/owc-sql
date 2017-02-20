package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class JDBCMySQLUnbufferedResultTable extends UnbufferedResultTable {
    @Nullable
    private ResultSet resultSet;

    JDBCMySQLUnbufferedResultTable(Map<String, UnbufferedResultColumn> columns, @Nullable ResultSet resultSet) {
        super(columns);
        this.resultSet = resultSet;
    }

    @Override
    public SQLResultRow<UnbufferedSQLResultColumn> fetchNextRow() throws IndexOutOfBoundsException {
        try {
            if (resultSet == null) {
                throw new IndexOutOfBoundsException();
            }

            Map<String, SQLResultField<UnbufferedSQLResultColumn>> row = new HashMap<>();

            for (Map.Entry<String,UnbufferedResultColumn> columnEntry : columns.entrySet()) {
                Object value = resultSet.getObject(columnEntry.getKey());
                if (resultSet.wasNull()) {
                    value = null;
                }
                row.put(
                    columnEntry.getKey(),
                    new UnbufferedResultField(
                        columnEntry.getKey(),
                        value
                    )
                );
            }

            UnbufferedResultRow result = new UnbufferedResultRow(row);
            if (!resultSet.next()) {
                resultSet.close();
                resultSet = null;
            }
            return result;
        } catch (java.sql.SQLException e) {
            throw new JDBCMySQLQueryException(e.getMessage() + " while fetching row", e);
        }
    }
}
