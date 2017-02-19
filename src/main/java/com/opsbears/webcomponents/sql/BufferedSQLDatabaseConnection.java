package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
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
    BufferedSQLResultTable query(String query, Map<Integer, Object> parameters) throws RuntimeException;
}
