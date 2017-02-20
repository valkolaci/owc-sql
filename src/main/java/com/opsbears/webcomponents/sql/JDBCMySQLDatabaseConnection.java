package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class JDBCMySQLDatabaseConnection implements MySQLDatabaseConnection {
    private Connection connection;

    public JDBCMySQLDatabaseConnection(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (java.sql.SQLException e) {
            throw new JDBCMySQLConnectionException(e);
        }
    }

    private PreparedStatement execute(String query, Map<Integer, Object> parameters, boolean unbuffered) throws SQLException {
        PreparedStatement stmt;
        if (unbuffered) {
            stmt = connection.prepareStatement(query);
            stmt.setFetchSize(Integer.MIN_VALUE);
        } else {
            stmt = connection.prepareStatement(query);
        }
        for (Map.Entry<Integer,Object> entry : parameters.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                stmt.setInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                stmt.setFloat(entry.getKey(), (Float) entry.getValue());
            } else if (entry.getValue() instanceof Date) {
                stmt.setDate(entry.getKey(), (Date) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                stmt.setString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                stmt.setBoolean(entry.getKey(), (Boolean) entry.getValue());
            } else {
                stmt.setString(entry.getKey(), entry.getValue().toString());
            }
        }
        stmt.execute();
        return stmt;
    }

    @Override
    public BufferedSQLResultTable query(String query, Map<Integer, Object> parameters) {
        try {
            @Nullable
            ResultSet resultSet = execute(query, parameters, false).getResultSet();
            if (resultSet == null) {
                return new BufferedResultTable(new ArrayList<>(), new HashMap<>());
            }

            List<BufferedResultRow> rows = new ArrayList<>();
            Map<String, BufferedResultColumn> columns = new HashMap<>();
            Map<String, List<BufferedResultField>> columnFields = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();

            do {
                Map<String, SQLResultField<BufferedSQLResultColumn>> rowFields = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    Object value = resultSet.getObject(i);
                    if (resultSet.wasNull()) {
                        value = null;
                    }
                    BufferedResultField field = new BufferedResultField(
                        metaData.getColumnLabel(i),
                        value
                    );
                    rowFields.put(metaData.getColumnLabel(i), field);
                    if (!columnFields.containsKey(metaData.getColumnLabel(i))) {
                        columnFields.put(metaData.getColumnLabel(i), new ArrayList<>());
                    }
                    columnFields.get(metaData.getColumnLabel(i)).add(field);
                }
                rows.add(new BufferedResultRow(rowFields));
            } while (resultSet.next());

            for (Map.Entry<String, List<BufferedResultField>> entry : columnFields.entrySet()) {
                columns.put(entry.getKey(), new BufferedResultColumn(entry.getKey(), entry.getValue()));
            }

            return new BufferedResultTable(rows, columns);
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException(query, e);
        }
    }

    @Override
    public UnbufferedSQLResultTable queryUnbuffered(String query, Map<Integer, Object> parameters) {
        try {
            ResultSet resultSet = execute(query, parameters, true).getResultSet();
            if (resultSet == null) {
                return new JDBCMySQLUnbufferedResultTable(new HashMap<>(), null);
            }
            ResultSetMetaData                   metaData  = resultSet.getMetaData();
            Map<String, UnbufferedResultColumn> columnMap = new HashMap<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnMap.put(metaData.getColumnLabel(i), new UnbufferedResultColumn(metaData.getColumnLabel(i)));
            }

            return new JDBCMySQLUnbufferedResultTable(columnMap, resultSet);
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException(query, e);
        }
    }
}
