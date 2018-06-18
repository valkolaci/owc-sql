package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.HSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.MySQLDatabaseConnection;
import com.opsbears.webcomponents.sql.migration.HSQLMigration;
import com.opsbears.webcomponents.sql.migration.MySQLMigration;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TestHSQLMigration implements HSQLMigration {
    @Override
    public void execute(HSQLDatabaseConnection connection) {
        connection.query(
            "CREATE TABLE migrate_test (\n" +
            "  id INTEGER IDENTITY PRIMARY KEY,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE,\n" +
            "  bool_field BOOLEAN\n" +
            ")\n"
        );
    }
}
