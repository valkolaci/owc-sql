package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HSQLConnectionFactory extends ConnectionFactory<HSQLDatabaseConnection> {
    HSQLDatabaseConnection getConnection();
    HSQLDatabaseConnection getConnection(String name);
}
