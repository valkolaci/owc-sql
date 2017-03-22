package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface BufferedUnbufferedDatabaseConnection extends BufferedSQLDatabaseConnection, UnbufferedSQLDatabaseConnection {
}
