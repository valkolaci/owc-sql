package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class JDBCHSQLConnectionFactory implements HSQLConnectionFactory {
    private ThreadLocal<Map<String, JDBCHSQLDatabaseConnection>> connections = new ThreadLocal<>();
    private Map<String, JDBCHSQLConnectionConfiguration> configurationMap;
    private final Collection<Consumer<HSQLDatabaseConnection>> createdCallbacks = new ArrayList<>();
    private final Collection<Consumer<HSQLDatabaseConnection>> closedCallbacks = new ArrayList<>();

    public JDBCHSQLConnectionFactory(Map<String, JDBCHSQLConnectionConfiguration> configurationMap) {
        this.configurationMap = configurationMap;
        connections.set(new HashMap<>());
    }

    public HSQLDatabaseConnection getConnection() {
        return getConnection("default");
    }


    @Override
    public HSQLDatabaseConnection getConnection(String name) {
        boolean connectionCreated = false;
        if (connections.get() == null) {
            connections.set(new HashMap<>());
        }
        if (!configurationMap.containsKey(name)) {
            throw new IndexOutOfBoundsException("No such MySQL connection configuration: " + name);
        }
        JDBCHSQLConnectionConfiguration configuration = configurationMap.get(name);
        if (!connections.get().containsKey(name)) {
            connectionCreated = true;
            connections.get().put(name, new JDBCHSQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }
        try {
            connections.get().get(name).query("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS", new HashMap<>());
        } catch (MySQLException e) {
            connectionCreated = true;
            for (Consumer<HSQLDatabaseConnection> callable : closedCallbacks) {
                callable.accept(connections.get().get(name));
            }
            connections.get().put(name, new JDBCHSQLDatabaseConnection(configuration.getJdbcUrl(), configuration.getUsername(), configuration.getPassword()));
        }

        if (connectionCreated) {
            for (Consumer<HSQLDatabaseConnection> callable : createdCallbacks) {
                callable.accept(connections.get().get(name));
            }
        }
        return connections.get().get(name);
    }

    @Override
    public void registerConnectionCreatedCallback(Consumer<HSQLDatabaseConnection> callback) {
        createdCallbacks.add(callback);
    }

    @Override
    public void registerConnectionDestroyedCallback(Consumer<HSQLDatabaseConnection> callback) {
        closedCallbacks.add(callback);
    }
}
