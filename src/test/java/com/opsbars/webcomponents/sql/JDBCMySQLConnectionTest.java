package com.opsbars.webcomponents.sql;

import com.opsbears.webcomponents.sql.JDBCMySQLDatabaseConnection;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.util.Date;

@ParametersAreNonnullByDefault
public class JDBCMySQLConnectionTest {
    private JDBCMySQLDatabaseConnection getConnection() {
        JDBCMySQLDatabaseConnection connection = new JDBCMySQLDatabaseConnection(
            "jdbc:mysql://localhost/test?characterEncoding=utf8&useUnicode=yes",
            "test",
            ""
        );
        connection.query(
            "DROP TABLE IF EXISTS test"
        );
        connection.query(
            "CREATE TABLE IF NOT EXISTS test (\n" +
            "  id BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE(8,2),\n" +
            "  bool_field BOOL\n" +
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
