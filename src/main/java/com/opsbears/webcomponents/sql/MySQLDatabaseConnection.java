package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MySQLDatabaseConnection extends BufferedUnbufferedDatabaseConnection, TransactionAwareDatabaseConnection {

}
