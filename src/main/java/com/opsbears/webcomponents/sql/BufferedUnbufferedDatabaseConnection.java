package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface BufferedUnbufferedDatabaseConnection extends BufferedSQLDatabaseConnection, UnbufferedSQLDatabaseConnection {

}
