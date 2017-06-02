package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TransactionAwareDatabaseConection {
    void startTransaction() throws TransactionAlreadyStartedException;
    void commit() throws TransactionNotStartedException;
    void rollback() throws TransactionNotStartedException;
}