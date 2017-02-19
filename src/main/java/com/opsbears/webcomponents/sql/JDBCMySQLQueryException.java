package com.opsbears.webcomponents.sql;

public class JDBCMySQLQueryException extends JDBCMySQLException {
    private String state;
    private String driverMessage;
    private int driverErrorCode;
    private String query;

    JDBCMySQLQueryException(String query, java.sql.SQLException e) {
        super(e);
        this.query      = query;
        driverErrorCode = e.getErrorCode();
        driverMessage   = e.getMessage();
        state           = e.getSQLState();
    }

    @Override
    public String getSQLState() {
        return state;
    }

    @Override
    public String getDriverMessage() {
        return driverMessage;
    }

    @Override
    public Integer getDriverErrorCode() {
        return driverErrorCode;
    }

    @Override
    public String getQuery() {
        return query;
    }
}
