package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;

@ParametersAreNonnullByDefault
public interface HSQLDatabaseConnection extends BufferedUnbufferedDatabaseConnection {
    public XAResource getXAResource() throws SQLException;
}
