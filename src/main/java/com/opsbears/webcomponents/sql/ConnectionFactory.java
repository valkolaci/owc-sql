package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ConnectionFactory<T extends BufferedUnbufferedDatabaseConnection> {
    T getConnection();
    T getConnection(String name);
}
