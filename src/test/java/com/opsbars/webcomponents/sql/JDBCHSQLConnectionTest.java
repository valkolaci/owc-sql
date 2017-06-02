package com.opsbars.webcomponents.sql;

import com.opsbears.webcomponents.sql.JDBCMySQLDatabaseConnection;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.util.Date;

@ParametersAreNonnullByDefault
public class JDBCHSQLConnectionTest {
    private JDBCMySQLDatabaseConnection getConnection() {
        JDBCMySQLDatabaseConnection connection = new JDBCMySQLDatabaseConnection(
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
