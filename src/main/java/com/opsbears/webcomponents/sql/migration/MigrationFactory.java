package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface MigrationFactory<T extends BufferedSQLDatabaseConnection> {
    List<Migration<T>> createMigrations();
}
