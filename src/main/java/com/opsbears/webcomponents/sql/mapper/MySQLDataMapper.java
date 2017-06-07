package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.MySQLConnectionFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@ParametersAreNonnullByDefault
public class MySQLDataMapper extends AbstractDataMapper {
    private MySQLConnectionFactory factory;

    public MySQLDataMapper(MySQLConnectionFactory factory) {
        this.factory = factory;
    }

    protected BufferedSQLDatabaseConnection getConnection() {
        return factory.getConnection();
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        Map<String, Object> parameters,
        @Nullable String orderBy,
        @Nullable OrderDirection orderDirection,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        Map<Integer,Object> sqlParameters = new HashMap<>();
        String sql = "SELECT\n";
        List<String> columns = new ArrayList<>();
        for (Method method : entityClass.getMethods()) {
            Column annotation = method.getAnnotation(Column.class);
            if (annotation != null) {
                columns.add("  " + annotation.value());
            }
        }
        sql += String.join(",\n", columns) + "\n";
        sql += "FROM\n";
        sql += "  " + entityClass.getAnnotation(Table.class).value() + "\n";
        if (!parameters.isEmpty()) {
            sql += "WHERE\n";
            List<String> conditions = new ArrayList<>();
            int          i          = 0;
            for (String parameter : parameters.keySet()) {
                conditions.add("  " + parameter + "=?\n");
                sqlParameters.put(i++, parameters.get(parameter));
            }
            sql += String.join("  AND\n", conditions);
        }
        if (orderBy != null && orderDirection != null) {
            sql += "ORDER BY " + orderBy + " " + orderDirection.toString() + " ";
        }
        if (limit != null) {
            sql += "LIMIT ";
            if (offset != null) {
                sql += offset + ", " + limit;
            } else {
                sql += limit;
            }
        }
        return loadByQuery(entityClass, sql, sqlParameters);
    }

    public void store(Object entity) {
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
