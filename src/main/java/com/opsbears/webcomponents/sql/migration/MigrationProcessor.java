package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract public class MigrationProcessor<T extends BufferedUnbufferedDatabaseConnection> implements Runnable {
    private final ConnectionFactory<T> connectionFactory;
    private final String               connectionName;
    private final MigrationFactory<T>  migrationFactory;

    public MigrationProcessor(
        ConnectionFactory<T> connectionFactory,
        String connectionName,
        MigrationFactory<T> migrationFactory
    ) {
        this.connectionFactory = connectionFactory;
        this.connectionName = connectionName;
        this.migrationFactory = migrationFactory;
    }

    abstract protected void createMigrationsTable(T connection);

    abstract protected boolean migrationAlreadyExecuted(T connection, Migration<T> migration);

    abstract protected void markMigrationExecuted(T connection, Migration<T> migration);

    @Override
    public void run() {
        T connection = connectionFactory.getConnection(connectionName);
        createMigrationsTable(connection);

        for (Migration<T> migration : migrationFactory.createMigrations()) {
            if (!migrationAlreadyExecuted(connection, migration)) {
                migration.execute(connection);
                markMigrationExecuted(connection, migration);
            }
        }
    }
}
