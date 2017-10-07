package com.opsbears.webcomponents.sql;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface UnbufferedSQLDatabaseConnection {
    /**
     * Queries the database with the given query in an unbuffered manner (the result can only be read once). This is
     * useful when you want to read a large data set.
     *
     * The parameters will be inserted in the places denoted with question marks in an SQL injection safe way. (?)
     *
     * @param query
     * @param parameters
     *
     * @return UnbufferedSQLResultTable
     */
    UnbufferedSQLResultTable queryUnbuffered(String query, Object... parameters);

    /**
     * Queries the database with the given query in an unbuffered manner (the result can only be read once). This is
     * useful when you want to read a large data set.
     *
     * The parameters will be inserted in the places denoted with question marks in an SQL injection safe way. (?)
     *
     * @param transaction
     * @param query
     * @param parameters
     *
     * @return UnbufferedSQLResultTable
     */
    UnbufferedSQLResultTable queryUnbuffered(Transaction transaction, String query, Object... parameters);

    /**
     * Queries the database with the given query in an unbuffered manner (the result can only be read once). This is
     * useful when you want to read a large data set.
     *
     * The parameters will be inserted in the places denoted with question marks in an SQL injection safe way. (?)
     *
     * @param query
     * @param parameters
     *
     * @return UnbufferedSQLResultTable
     */
    UnbufferedSQLResultTable queryUnbuffered(String query, Map<Integer, Object> parameters) throws RuntimeException;

    /**
     * Queries the database with the given query in an unbuffered manner (the result can only be read once). This is
     * useful when you want to read a large data set.
     *
     * The parameters will be inserted in the places denoted with question marks in an SQL injection safe way. (?)
     *
     * @param transaction
     * @param query
     * @param parameters
     *
     * @return UnbufferedSQLResultTable
     */
    UnbufferedSQLResultTable queryUnbuffered(@Nullable Transaction transaction, String query, Map<Integer, Object> parameters) throws RuntimeException;
}