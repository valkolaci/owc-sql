package com.opsbears.webcomponents.sql;

import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class JDBCHSQLConnectionTest {
    private JDBCHSQLDatabaseConnection getConnection() {
        JDBCHSQLDatabaseConnection connection = new JDBCHSQLDatabaseConnection(
            "jdbc:hsqldb:mem:test",
            "test",
            ""
        );
        connection.query(
            "DROP TABLE IF EXISTS test"
        );
        connection.query(
            "CREATE TABLE IF NOT EXISTS test (\n" +
            "  id INTEGER IDENTITY PRIMARY KEY,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE,\n" +
            "  bool_field BOOLEAN\n" +
            ")\n"
        );
        return connection;
    }

    @Test
    public void testInsertQuery() {
        getConnection().query(
            "INSERT INTO test (\n" +
            "  text_field,\n" +
            "  date_field,\n" +
            "  float_field,\n" +
            "  bool_field\n" +
            ") VALUES (\n" +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?" +
            ")",
            "test",
            new Date(),
            1.2,
            false
        );
        getConnection().query(
            "INSERT INTO test (\n" +
                "  text_field,\n" +
                "  date_field,\n" +
                "  float_field,\n" +
                "  bool_field\n" +
                ") VALUES (\n" +
                "  ?," +
                "  ?," +
                "  ?," +
                "  ?" +
                ")",
            "test",
            LocalDateTime.now(),
            1.2,
            false
        );
    }

}