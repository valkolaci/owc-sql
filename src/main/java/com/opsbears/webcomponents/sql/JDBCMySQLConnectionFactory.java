package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class JDBCMySQLConnectionFactory implements MySQLConnectionFactory {
    private ThreadLocal<Map<String, JDBCMySQLDatabaseConnection>> connections = new ThreadLocal<>();
    private Map<String, JDBCMySQLConnectionConfiguration> configurationMap;

    public JDBCMySQLConnectionFactory(Map<String, JDBCMySQLConnectionConfiguration> configurationMap) {
        this.configurationMap = configurationMap;
        connections.set(new HashMap<>());
    }

    public MySQLDatabaseConnection getConnection() {
        return getConnection("default");
    }

    @Override
    public MySQLDatabaseConnection getConnection(String name) {
        if (connections.get() == null) {
            connections.set(new HashMap<>());
        }
        if (!configurationMap.containsKey(name)) {
            throw new IndexOutOfBoundsException("No such MySQL connection configuration: " + name);
        }
        JDBCMySQLConnectionConfiguration configuration = configurationMap.get(name);
        if (!connections.get().containsKey(name)) {
            connections.get().put(name, new JDBCMySQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }
        try {
            connections.get().get(name).query("SELECT 1=1");
        } catch (MySQLException e) {
            connections.get().put(name, new JDBCMySQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }
        return connections.get().get(name);
    }

}
