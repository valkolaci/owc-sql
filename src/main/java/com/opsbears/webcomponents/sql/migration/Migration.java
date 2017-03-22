package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Migration<T extends BufferedSQLDatabaseConnection> {
    void execute(T connection);
}
