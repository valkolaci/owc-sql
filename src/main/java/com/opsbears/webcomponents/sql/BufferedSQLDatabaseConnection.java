package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface BufferedSQLDatabaseConnection {
    /**
     * Queries the database with the given query. The parameters will be inserted in the places denoted with question
     * marks in an SQL injection safe way. (?)
     *
     * @param query
     * @param parameters
     *
     * @return BufferedSQLResultTable
     */
    BufferedSQLResultTable query(String query, Object... parameters);

    /**
     * Queries the database with the given query. The parameters will be inserted in the places denoted with question
     * marks in an SQL injection safe way. (?)
     *
     * @param transaction
     * @param query
     * @param parameters
     *
     * @return BufferedSQLResultTable
     */
    BufferedSQLResultTable query(@Nullable Transaction transaction, String query, Object... parameters);

    /**
     * Queries the database with the given query. The parameters will be inserted in the places denoted with question
     * marks in an SQL injection safe way. (?)
     *
     * @param query
     * @param parameters
     *
     * @return BufferedSQLResultTable
     */
    BufferedSQLResultTable query(String query, Map<Integer, Object> parameters) throws RuntimeException;

    /**
     * Queries the database with the given query. The parameters will be inserted in the places denoted with question
     * marks in an SQL injection safe way. (?)
     *
     * @param transaction
     * @param query
     * @param parameters
     *
     * @return BufferedSQLResultTable
     */
    BufferedSQLResultTable query(@Nullable Transaction transaction, String query, Map<Integer, Object> parameters) throws RuntimeException;
}
