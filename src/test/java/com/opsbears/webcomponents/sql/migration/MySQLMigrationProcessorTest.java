package com.opsbears.webcomponents.sql.migration;

import com.opsbears.webcomponents.sql.*;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MySQLMigrationProcessorTest {
    private MySQLConnectionFactory getConnectionFactory() {
        Map<String,JDBCMySQLConnectionConfiguration> connectionConfigurationMap = new HashMap<>();
        connectionConfigurationMap.put("default", new JDBCMySQLConnectionConfiguration(
            "jdbc:mysql://localhost/test?characterEncoding=utf8&useUnicode=yes",
            "test",
            ""
        ));

        return new JDBCMySQLConnectionFactory(
            connectionConfigurationMap
        );
    }

    @Test
    public void testMigration() {
        MySQLMigrationFactory factory = new MySQLMigrationFactory() {

            @Override
            public List<Migration<MySQLDatabaseConnection>> createMigrations() {
                List<Migration<MySQLDatabaseConnection>> migrations = new ArrayList<>();
                migrations.add(new TestMySQLMigration());
                return migrations;
            }
        };
        MySQLConnectionFactory connectionFactory = getConnectionFactory();
        MigrationProcessor<MySQLDatabaseConnection> processor = new MySQLMigrationProcessor(
            connectionFactory,
            "default",
            factory
        );
        processor.run();
        connectionFactory.getConnection().query("SELECT * FROM migrate_test");
    }
}
