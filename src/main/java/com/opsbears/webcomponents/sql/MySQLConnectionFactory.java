package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MySQLConnectionFactory extends ConnectionFactory<MySQLDatabaseConnection> {
    MySQLDatabaseConnection getConnection();
    MySQLDatabaseConnection getConnection(String name);
}
