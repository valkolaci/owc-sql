package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.XAConnection;
import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

@ParametersAreNonnullByDefault
abstract public class JDBCDatabaseConnection implements BufferedUnbufferedDatabaseConnection {
    protected Connection connection;
    protected XAConnection xaConnection = null;

    @Nullable
    public XAResource getXAResource() throws SQLException {
        return xaConnection == null?null:xaConnection.getXAResource();
    }

    private PreparedStatement execute(
        @Nullable Transaction transaction,
        String query,
        Map<Integer, Object> parameters,
        boolean unbuffered
    ) throws SQLException {
        if (xaConnection == null && transaction != null) {
            throw new MissingTransactionSupportException();
        }
        if (xaConnection != null && transaction != null) {
            try {
                transaction.enlistResource(xaConnection.getXAResource());
            } catch (RollbackException | SystemException e) {
                throw new SQLException(e);
            }
        }

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
            } else if (entry.getValue().getClass().equals(int.class)) {
                stmt.setInt(columnIndex, (int) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                stmt.setFloat(columnIndex, (Float) entry.getValue());
            } else if (entry.getValue().getClass().equals(float.class)) {
                stmt.setFloat(columnIndex, (float) entry.getValue());
            } else if (entry.getValue() instanceof Timestamp) {
                stmt.setTimestamp(columnIndex, (Timestamp) entry);
            } else if (entry.getValue() instanceof Date) {
                stmt.setTimestamp(columnIndex, new java.sql.Timestamp(((Date)entry.getValue()).getTime()));
            } else if (entry.getValue() instanceof LocalDateTime) {
                stmt.setTimestamp(columnIndex, Timestamp.valueOf(((LocalDateTime)entry.getValue())));
            } else if (entry.getValue() instanceof String) {
                stmt.setString(columnIndex, (String) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                stmt.setBoolean(columnIndex, (Boolean) entry.getValue());
            } else if (entry.getValue().getClass().equals(boolean.class)) {
                stmt.setBoolean(columnIndex, (boolean) entry.getValue());
            } else if (entry.getValue() instanceof BigDecimal) {
                stmt.setBigDecimal(columnIndex, (BigDecimal) entry.getValue());
            } else if (entry.getValue() instanceof UUID) {
                stmt.setString(columnIndex, entry.getValue().toString());
            } else if (entry.getValue().getClass().equals(byte.class)) {
                stmt.setByte(columnIndex, (byte) entry.getValue());
            } else if (entry.getValue().getClass().equals(byte[].class)) {
                stmt.setBytes(columnIndex, (byte[]) entry.getValue());
            } else if (entry.getValue() instanceof InputStream) {
                stmt.setBlob(columnIndex, (InputStream) entry.getValue());
            } else if (entry.getValue() instanceof Blob) {
                stmt.setBlob(columnIndex, (Blob) entry.getValue());
            } else {
                stmt.setString(columnIndex, entry.getValue().toString());
            }
        }
        stmt.execute();
        return stmt;
    }

    @Override
    public BufferedSQLResultTable query(String query, Object... parameters) {
        return query(null, query, parameters);
    }

    @Override
    public BufferedSQLResultTable query(@Nullable Transaction transaction, String query, Object... parameters) {
        Map<Integer,Object> newParameters = new HashMap<>();
        int i = 0;
        for (Object parameter : parameters) {
            newParameters.put(i++, parameter);
        }
        return query(transaction, query, newParameters);
    }

        @Override
    public BufferedSQLResultTable query(
        String query,
        Map<Integer, Object> parameters
    ) {
        return query(null, query, parameters);
    }

    @Override
    public BufferedSQLResultTable query(
        @Nullable Transaction transaction,
        String query,
        Map<Integer, Object> parameters
    ) {
        try {
            @Nullable
            ResultSet resultSet = execute(transaction, query, parameters, false).getResultSet();
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
        return queryUnbuffered(null, query, parameters);
    }

    @Override
    public UnbufferedSQLResultTable queryUnbuffered(@Nullable Transaction transaction, String query, Object... parameters) {
        Map<Integer,Object> newParameters = new HashMap<>();
        int i = 0;
        for (Object parameter : parameters) {
            newParameters.put(i++, parameter);
        }
        return queryUnbuffered(transaction, query, newParameters);
    }

    @Override
    public UnbufferedSQLResultTable queryUnbuffered(String query, Map<Integer, Object> parameters) {
        return queryUnbuffered(null, query, parameters);
    }

    @Override
    public UnbufferedSQLResultTable queryUnbuffered(
        @Nullable Transaction transaction,
        String query,
        Map<Integer, Object> parameters
    ) {
        try {
            ResultSet resultSet = execute(transaction, query, parameters, true).getResultSet();
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
}
