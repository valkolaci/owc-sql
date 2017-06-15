package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.Date;

@ParametersAreNonnullByDefault
public class JDBCMySQLDatabaseConnection extends JDBCDatabaseConnection implements MySQLDatabaseConnection {
    private boolean transactionStarted = false;

    public JDBCMySQLDatabaseConnection(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL + "&serverTimezone=GMT", username, password);
        } catch (SQLException e) {
            throw new JDBCMySQLConnectionException(e);
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
