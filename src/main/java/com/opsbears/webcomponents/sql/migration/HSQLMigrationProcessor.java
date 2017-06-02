package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.ConnectionFactory;
import com.opsbears.webcomponents.sql.HSQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HSQLMigrationProcessor extends MigrationProcessor<HSQLDatabaseConnection> {
    public HSQLMigrationProcessor(
        ConnectionFactory<HSQLDatabaseConnection> connectionFactory,
        String connectionName,
        MigrationFactory<HSQLDatabaseConnection> migrationFactory
    ) {
        super(connectionFactory, connectionName, migrationFactory);
    }

    protected void createMigrationsTable(HSQLDatabaseConnection connection) {
        connection.query(
            "CREATE TABLE IF NOT EXISTS migrations (\n" +
            "  class_name VARCHAR(255) PRIMARY KEY,\n" +
            "  applied_at DATETIME\n" +
            ")"
        );
    }

    protected boolean migrationAlreadyExecuted(HSQLDatabaseConnection connection, Migration<HSQLDatabaseConnection> migration) {
        BufferedSQLResultTable result = connection.query(
            "SELECT class_name FROM migrations WHERE class_name=?",
            migration.getClass().getName()
        );
        return result.size() > 0;
    }

    protected void markMigrationExecuted(HSQLDatabaseConnection connection, Migration<HSQLDatabaseConnection> migration) {
        connection.query(
            "INSERT INTO migrations (class_name, applied_at) VALUES (?, CURRENT_TIMESTAMP)",
            migration.getClass().getName()
        );
    }
}
