package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.MySQLConnectionFactory;
import com.opsbears.webcomponents.typeconverter.TypeConverterChain;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@ParametersAreNonnullByDefault
public class MySQLDataMapper extends AbstractDataMapper {
    private MySQLConnectionFactory factory;

    public MySQLDataMapper(MySQLConnectionFactory factory, TypeConverterChain typeConverterChain) {
        super(typeConverterChain);
        this.factory = factory;
    }

    public MySQLDataMapper(MySQLConnectionFactory factory) {
        super();
        this.factory = factory;
    }

    protected BufferedSQLDatabaseConnection getConnection() {
        return factory.getConnection();
    }

    protected String transformColumName(String columnName) {
        return columnName;
    }

    public void store(Object entity) {
        store(null, entity);
    }

    public void store(@Nullable Transaction transaction, Object entity) {
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new RuntimeException("Missing @Table annotation on " + entity.getClass().getName());
        }

        List<String> columns = new ArrayList<>();
        Map<Integer,Object> values = new HashMap<>();
        List<String> placeholders = new ArrayList<>();

        int i = 0;
        for (Method method : entity.getClass().getMethods()) {
            Column annotation = method.getAnnotation(Column.class);
            if (annotation != null) {
                try {
                    columns.add(annotation.value());
                    values.put(i++, method.invoke(entity));
                    placeholders.add("?");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                        "Failed to fetch column value from " + entity.getClass().getName() + "." + method.getName() + "()"
                    );
                }
            }
        }
        List<String> updatePlaceholder = new ArrayList<>();
        for (Method method : entity.getClass().getMethods()) {
            Column annotation = method.getAnnotation(Column.class);
            if (annotation != null) {
                try {
                    updatePlaceholder.add(annotation.value() + "=?");
                    values.put(i++, method.invoke(entity));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                        "Failed to fetch column value from " + entity.getClass().getName() + "." + method.getName() + "()"
                    );
                }
            }
        }

        String query =
            "INSERT INTO " + entity.getClass().getAnnotation(Table.class).value() + " (" +
           String.join(", ", columns) +
            ") VALUES (" +
            String.join(", ", placeholders) +
            ") ON DUPLICATE KEY UPDATE " + String.join(", ", updatePlaceholder);

        factory.getConnection().query(query, values);
    }
}
