package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.MySQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MySQLMigrationFactory extends MigrationFactory<MySQLDatabaseConnection> {
}
