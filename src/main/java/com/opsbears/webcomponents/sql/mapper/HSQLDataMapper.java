package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.HSQLConnectionFactory;
import com.opsbears.webcomponents.sql.querybuilder.Condition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class HSQLDataMapper extends AbstractDataMapper {
    private HSQLConnectionFactory factory;

    public HSQLDataMapper(HSQLConnectionFactory factory) {
        this.factory = factory;
    }

    protected BufferedSQLDatabaseConnection getConnection() {
        return factory.getConnection();
    }

    protected String transformColumName(String columnName) {
        return columnName.toUpperCase();
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
                columns.add("  " + annotation.value().toUpperCase());
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

    @Override
    public void store(Object entity) {
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new RuntimeException("Missing @Table annotation on " + entity.getClass().getName());
        }

        List<String> columns = new ArrayList<>();
        Map<Integer,Object> values = new HashMap<>();
        List<String> placeholders = new ArrayList<>();
        List<String> mergeColumns = new ArrayList<>();

        int i = 0;
        for (Method method : entity.getClass().getMethods()) {
            Primary primaryAnnotation = method.getAnnotation(Primary.class);
            Column columnAnnotation = method.getAnnotation(Column.class);
            if (primaryAnnotation != null) {
                if (columnAnnotation == null) {
                    throw new InvalidAnnotationException("@Primary always has to be used in conjunction with @Column");
                }

                mergeColumns.add(columnAnnotation.value());
            }
        }

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

        if (mergeColumns.isEmpty()) {
            throw new RuntimeException("Cannot store in HSQLDB without at least one column with the @Primary annotation on entity " + entity.getClass().getName());
        }

        String query =
            "MERGE INTO " + entity.getClass().getAnnotation(Table.class).value() + " T\n" +
                "USING (VALUES " + String.join(", ", placeholders) + ") I (" + String.join(", ", columns) + ")\n" +
                "ON (" + String.join(", ", mergeColumns.stream().map(e -> "T." + e + "=I." + e).collect(Collectors.toList())) + ")\n" +
                "WHEN NOT MATCHED THEN INSERT (" + String.join(", ", columns) + ") VALUES (" + String.join(", ", columns.stream().map(e -> "I." + e).collect(Collectors.toList())) + ")\n" +
                "WHEN MATCHED THEN UPDATE SET " + String.join(", ", columns.stream().map(e -> "T." + e + "=I." + e).collect(Collectors.toList())) + "\n";

        factory.getConnection().query(query, values);
    }

    @Override
    public <T> long countBy(
        Class<T> entityClass,
        Condition condition
    ) {
        return 0;
    }
}
