package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.HSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.MySQLDatabaseConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HSQLMigration extends Migration<HSQLDatabaseConnection> {
    @Override
    void execute(HSQLDatabaseConnection connection);
}
