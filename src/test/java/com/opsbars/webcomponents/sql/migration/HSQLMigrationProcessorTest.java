package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.JDBCHSQLConnectionConfiguration;
import com.opsbears.webcomponents.sql.JDBCHSQLConnectionFactory;
import com.opsbears.webcomponents.sql.HSQLConnectionFactory;
import com.opsbears.webcomponents.sql.HSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.migration.*;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class HSQLMigrationProcessorTest {
    private HSQLConnectionFactory getConnectionFactory() {
        Map<String,JDBCHSQLConnectionConfiguration> connectionConfigurationMap = new HashMap<>();
        connectionConfigurationMap.put("default", new JDBCHSQLConnectionConfiguration(
            "jdbc:hsqldb:mem:test",
            "test",
            ""
        ));

        return new JDBCHSQLConnectionFactory(
            connectionConfigurationMap
        );
    }

    @Test
    public void testMigration() {
        HSQLMigrationFactory factory = new HSQLMigrationFactory() {

            @Override
            public List<Migration<HSQLDatabaseConnection>> createMigrations() {
                List<Migration<HSQLDatabaseConnection>> migrations = new ArrayList<>();
                migrations.add(new TestHSQLMigration());
                return migrations;
            }
        };
        HSQLConnectionFactory connectionFactory = getConnectionFactory();
        MigrationProcessor<HSQLDatabaseConnection> processor = new HSQLMigrationProcessor(
            connectionFactory,
            "default",
            factory
        );
        processor.run();
        connectionFactory.getConnection().query("SELECT * FROM migrate_test");
    }
}
