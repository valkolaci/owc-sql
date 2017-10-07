package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public interface ConnectionFactory<T extends BufferedUnbufferedDatabaseConnection> {
    T getConnection();
    T getConnection(String name);
    void registerConnectionCreatedCallback(Consumer<T> callback);
    void registerConnectionDestroyedCallback(Consumer<T> callback);
}
