package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;

@ParametersAreNonnullByDefault
public interface MySQLDatabaseConnection extends BufferedUnbufferedDatabaseConnection, TransactionAwareDatabaseConnection {
    public XAResource getXAResource() throws SQLException;
}
