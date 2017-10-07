package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class JDBCMySQLConnectionFactory implements MySQLConnectionFactory {
    private ThreadLocal<Map<String, JDBCMySQLDatabaseConnection>> connections = new ThreadLocal<>();
    private Map<String, JDBCMySQLConnectionConfiguration> configurationMap;
    private final Collection<Consumer<MySQLDatabaseConnection>> createdCallbacks = new ArrayList<>();
    private final Collection<Consumer<MySQLDatabaseConnection>> closedCallbacks = new ArrayList<>();

    public JDBCMySQLConnectionFactory(Map<String, JDBCMySQLConnectionConfiguration> configurationMap) {
        this.configurationMap = configurationMap;
        connections.set(new HashMap<>());
    }

    public MySQLDatabaseConnection getConnection() {
        return getConnection("default");
    }

    @Override
    public MySQLDatabaseConnection getConnection(String name) {
        boolean connectionCreated = false;
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
            connectionCreated = true;
            for (Consumer<MySQLDatabaseConnection> callable : closedCallbacks) {
                callable.accept(connections.get().get(name));
            }
            connections.get().put(name, new JDBCMySQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }

        if (connectionCreated) {
            for (Consumer<MySQLDatabaseConnection> callable : createdCallbacks) {
                callable.accept(connections.get().get(name));
            }
        }
        return connections.get().get(name);
    }

    @Override
    public void registerConnectionCreatedCallback(Consumer<MySQLDatabaseConnection> callback) {
        createdCallbacks.add(callback);
    }

    @Override
    public void registerConnectionDestroyedCallback(Consumer<MySQLDatabaseConnection> callback) {
        closedCallbacks.add(callback);
    }
}
