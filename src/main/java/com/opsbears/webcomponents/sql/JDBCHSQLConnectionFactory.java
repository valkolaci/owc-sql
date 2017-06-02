package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class JDBCHSQLConnectionFactory implements HSQLConnectionFactory {
    private ThreadLocal<Map<String, JDBCHSQLDatabaseConnection>> connections = new ThreadLocal<>();
    private Map<String, JDBCHSQLConnectionConfiguration> configurationMap;

    public JDBCHSQLConnectionFactory(Map<String, JDBCHSQLConnectionConfiguration> configurationMap) {
        this.configurationMap = configurationMap;
        connections.set(new HashMap<>());
    }

    public HSQLDatabaseConnection getConnection() {
        return getConnection("default");
    }

    @Override
    public HSQLDatabaseConnection getConnection(String name) {
        if (connections.get() == null) {
            connections.set(new HashMap<>());
        }
        if (!configurationMap.containsKey(name)) {
            throw new IndexOutOfBoundsException("No such MySQL connection configuration: " + name);
        }
        JDBCHSQLConnectionConfiguration configuration = configurationMap.get(name);
        if (!connections.get().containsKey(name)) {
            connections.get().put(name, new JDBCHSQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }
        try {
            connections.get().get(name).query("SELECT 1=1", new HashMap<>());
        } catch (MySQLException e) {
            connections.get().put(name, new JDBCHSQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }
        return connections.get().get(name);
    }
}
