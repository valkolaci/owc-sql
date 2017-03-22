package com.opsbars.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.MySQLDatabaseConnection;
import com.opsbears.webcomponents.sql.migration.MySQLMigration;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TestMySQLMigration implements MySQLMigration {
    @Override
    public void execute(MySQLDatabaseConnection connection) {
        connection.query(
            "CREATE TABLE migrate_test (\n" +
            "  id BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE(8,2),\n" +
            "  bool_field BOOL\n" +
            ")\n"
        );
    }
}
