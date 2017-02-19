package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract public class MySQLException extends SQLException {
    public MySQLException(String s) {
        super(s);
    }

    public MySQLException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MySQLException(Throwable throwable) {
        super(throwable);
    }

    abstract public String getSQLState();

    abstract public String getDriverMessage();

    abstract public Integer getDriverErrorCode();
}
