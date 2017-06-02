package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.HSQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HSQLMigrationFactory extends MigrationFactory<HSQLDatabaseConnection> {
}
