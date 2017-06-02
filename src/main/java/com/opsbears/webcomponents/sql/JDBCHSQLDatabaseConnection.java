package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.DriverManager;
import java.sql.SQLException;

@ParametersAreNonnullByDefault
public class JDBCHSQLDatabaseConnection extends JDBCDatabaseConnection implements HSQLDatabaseConnection {
    private boolean transactionStarted = false;

    public JDBCHSQLDatabaseConnection(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            throw new JDBCMySQLConnectionException(e);
        }
    }
}
