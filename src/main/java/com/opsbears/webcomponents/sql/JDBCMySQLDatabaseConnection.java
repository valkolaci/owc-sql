package com.opsbears.webcomponents.sql;

import com.mysql.cj.core.conf.url.HostInfo;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.MysqlXAConnection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.DriverManager;
import java.sql.SQLException;

@ParametersAreNonnullByDefault
public class JDBCMySQLDatabaseConnection extends JDBCDatabaseConnection implements MySQLDatabaseConnection {
    private boolean transactionStarted = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public JDBCMySQLDatabaseConnection(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL + "&serverTimezone=GMT", username, password);
            try {
                xaConnection = new MysqlXAConnection((ConnectionImpl) connection, true);
            } catch (ClassCastException e) {
                //No XA support, probably an old driver.
            }
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
