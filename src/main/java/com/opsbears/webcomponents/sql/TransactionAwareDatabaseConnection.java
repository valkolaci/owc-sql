package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Deprecated
public interface TransactionAwareDatabaseConnection {
    @Deprecated
    void startTransaction() throws TransactionAlreadyStartedException;
    @Deprecated
    void commit() throws TransactionNotStartedException;
    @Deprecated
    void rollback() throws TransactionNotStartedException;
}
