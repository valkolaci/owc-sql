package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MySQLConnectionFactory {
    MySQLDatabaseConnection getConnection();
    MySQLDatabaseConnection getConnection(String name);
}
