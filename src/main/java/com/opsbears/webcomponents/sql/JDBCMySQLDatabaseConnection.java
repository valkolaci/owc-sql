package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@ParametersAreNonnullByDefault
public class JDBCMySQLDatabaseConnection implements MySQLDatabaseConnection {
    private Connection connection;
    private boolean transactionStarted = false;

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
            int columnIndex = entry.getKey() + 1;
            if (entry.getValue() == null) {
                stmt.setNull(columnIndex, Types.NULL);
            } else if (entry.getValue() instanceof Integer) {
                stmt.setInt(columnIndex, (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                stmt.setFloat(columnIndex, (Float) entry.getValue());
            } else if (entry.getValue() instanceof Timestamp) {
                stmt.setTimestamp(columnIndex, (Timestamp) entry);
            } else if (entry.getValue() instanceof Date) {
                stmt.setString(columnIndex, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) entry.getValue()));
            } else if (entry.getValue() instanceof String) {
                stmt.setString(columnIndex, (String) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                stmt.setBoolean(columnIndex, (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof BigDecimal) {
                stmt.setBigDecimal(columnIndex, (BigDecimal) entry.getValue());
            } else if (entry.getValue() instanceof UUID) {
                stmt.setString(columnIndex, entry.getValue().toString());
            } else {
                stmt.setString(columnIndex, entry.getValue().toString());
            }
        }
        stmt.execute();
        return stmt;
    }

    @Override
    public BufferedSQLResultTable query(String query, Object... parameters) {
        Map<Integer,Object> newParameters = new HashMap<>();
        int i = 0;
        for (Object parameter : parameters) {
            newParameters.put(i++, parameter);
        }
        return query(query, newParameters);
    }

    @Override
    public BufferedSQLResultTable query(String query, Map<Integer, Object> parameters) {
        try {
            @Nullable
            ResultSet resultSet = execute(query, parameters, false).getResultSet();
            if (resultSet == null) {
                return new BufferedResultTable(new ArrayList<>(), new HashMap<>());
            }

            List<BufferedResultRow>                rows         = new ArrayList<>();
            Map<String, BufferedResultColumn>      columns      = new HashMap<>();
            Map<String, List<BufferedResultField>> columnFields = new HashMap<>();
            ResultSetMetaData                      metaData     = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, SQLResultField<BufferedSQLResultColumn>> rowFields = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    int columnIndex = i + 1;
                    Object value = resultSet.getObject(columnIndex);
                    if (resultSet.wasNull()) {
                        value = null;
                    }
                    BufferedResultField field = new BufferedResultField(
                        metaData.getColumnLabel(columnIndex),
                        value
                    );
                    rowFields.put(metaData.getColumnLabel(columnIndex), field);
                    if (!columnFields.containsKey(metaData.getColumnLabel(columnIndex))) {
                        columnFields.put(metaData.getColumnLabel(columnIndex), new ArrayList<>());
                    }
                    columnFields.get(metaData.getColumnLabel(columnIndex)).add(field);
                }
                rows.add(new BufferedResultRow(rowFields));
            }

            for (Map.Entry<String, List<BufferedResultField>> entry : columnFields.entrySet()) {
                columns.put(entry.getKey(), new BufferedResultColumn(entry.getKey(), entry.getValue()));
            }

            return new BufferedResultTable(rows, columns);
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException(query, e);
        }
    }

    @Override
    public UnbufferedSQLResultTable queryUnbuffered(String query, Object... parameters) {
        Map<Integer,Object> newParameters = new HashMap<>();
        int i = 0;
        for (Object parameter : parameters) {
            newParameters.put(i++, parameter);
        }
        return queryUnbuffered(query, newParameters);
    }

    @Override
    public UnbufferedSQLResultTable queryUnbuffered(String query, Map<Integer, Object> parameters) {
        try {
            ResultSet resultSet = execute(query, parameters, true).getResultSet();
            if (resultSet == null) {
                return new JDBCMySQLUnbufferedResultTable(new HashMap<>(), null);
            }
            ResultSetMetaData                   metaData  = resultSet.getMetaData();
            Map<String, UnbufferedSQLResultColumn> columnMap = new HashMap<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnMap.put(metaData.getColumnLabel(i), new UnbufferedResultColumn(metaData.getColumnLabel(i)));
            }

            return new JDBCMySQLUnbufferedResultTable(columnMap, resultSet);
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException(query, e);
        }
    }

    public void finalize() {
        try {
            connection.close();
        } catch (SQLException ignored) {

        }
    }

    @Override
    public void startTransaction() throws TransactionAlreadyStartedException {
        if (transactionStarted) {
            throw new TransactionAlreadyStartedException();
        }
        try {
            connection.setAutoCommit(false);
            transactionStarted = true;
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException("SET autocommit=0", e);
        }
    }

    @Override
    public void commit() throws TransactionNotStartedException {
        if (!transactionStarted) {
            throw new TransactionNotStartedException();
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException("COMMIT", e);
        }
        transactionStarted = false;
    }

    @Override
    public void rollback() throws TransactionNotStartedException {
        if (!transactionStarted) {
            throw new TransactionNotStartedException();
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new JDBCMySQLQueryException("ROLLBACK", e);
        }
        transactionStarted = false;
    }
}
