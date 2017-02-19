package com.opsbears.webcomponents.sql;

class JDBCMySQLConnectionException extends JDBCMySQLException {
    private Integer driverErrorCode;
    private String driverErrorMessage;
    private String sqlState;
    private String query;

    public JDBCMySQLConnectionException(java.sql.SQLException original) {
        super(original);
        driverErrorCode = original.getErrorCode();
        driverErrorMessage = original.getMessage();
        sqlState = original.getSQLState();
        query = "";
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getSQLState() {
        return sqlState;
    }

    @Override
    public String getDriverMessage() {
        return driverErrorMessage;
    }

    @Override
    public Integer getDriverErrorCode() {
        return driverErrorCode;
    }
}
