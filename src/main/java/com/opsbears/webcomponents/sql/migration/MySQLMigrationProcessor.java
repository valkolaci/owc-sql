package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.ConnectionFactory;
import com.opsbears.webcomponents.sql.MySQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MySQLMigrationProcessor extends MigrationProcessor<MySQLDatabaseConnection> {
    public MySQLMigrationProcessor(
        ConnectionFactory<MySQLDatabaseConnection> connectionFactory,
        String connectionName,
        MigrationFactory<MySQLDatabaseConnection> migrationFactory
    ) {
        super(connectionFactory, connectionName, migrationFactory);
    }

    protected void createMigrationsTable(MySQLDatabaseConnection connection) {
        connection.query(
            "CREATE TABLE IF NOT EXISTS migrations (\n" +
            "  class_name VARCHAR(255) PRIMARY KEY,\n" +
            "  applied_at DATETIME\n" +
            ")"
        );
    }

    protected boolean migrationAlreadyExecuted(MySQLDatabaseConnection connection, Migration<MySQLDatabaseConnection> migration) {
        BufferedSQLResultTable result = connection.query(
            "SELECT class_name FROM migrations WHERE class_name=?",
            migration.getClass().getName()
        );
        return result.size() > 0;
    }

    protected void markMigrationExecuted(MySQLDatabaseConnection connection, Migration<MySQLDatabaseConnection> migration) {
        connection.query(
            "INSERT INTO migrations (class_name, applied_at) VALUES (?, CURRENT_TIMESTAMP)",
            migration.getClass().getName()
        );
    }
}
