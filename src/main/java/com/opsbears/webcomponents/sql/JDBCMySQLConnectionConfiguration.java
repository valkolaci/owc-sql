package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JDBCMySQLConnectionConfiguration {
    private String jdbcUrl;
    private String username;
    private String password;

    public JDBCMySQLConnectionConfiguration(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    String getJdbcUrl() {
        return jdbcUrl;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}
