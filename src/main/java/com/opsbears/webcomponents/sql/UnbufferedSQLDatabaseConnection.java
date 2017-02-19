package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
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
    UnbufferedSQLResultTable queryUnbuffered(String query, Map<Integer, Object> parameters) throws RuntimeException;
}