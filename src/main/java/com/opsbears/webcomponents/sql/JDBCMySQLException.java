package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract class JDBCMySQLException extends MySQLException {
    JDBCMySQLException(java.sql.SQLException original) {
        super(original.getMessage(), original);
    }

    JDBCMySQLException(String message, java.sql.SQLException original) {
        super(message, original);
    }
}
