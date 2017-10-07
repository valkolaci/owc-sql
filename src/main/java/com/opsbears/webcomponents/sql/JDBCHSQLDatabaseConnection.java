package com.opsbears.webcomponents.sql;

import org.hsqldb.jdbc.JDBCConnection;
import org.hsqldb.jdbc.pool.JDBCXAConnection;
import org.hsqldb.jdbc.pool.JDBCXADataSource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.DriverManager;
import java.sql.SQLException;

@ParametersAreNonnullByDefault
public class JDBCHSQLDatabaseConnection extends JDBCDatabaseConnection implements HSQLDatabaseConnection {
    private boolean transactionStarted = false;

    public JDBCHSQLDatabaseConnection(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            xaConnection = new JDBCXAConnection(new JDBCXADataSource(), (JDBCConnection) connection);
        } catch (SQLException e) {
            throw new JDBCMySQLConnectionException(e);
        }
    }
}
