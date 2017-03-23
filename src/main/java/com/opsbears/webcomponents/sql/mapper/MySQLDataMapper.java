package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.MySQLConnectionFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.util.*;

@ParametersAreNonnullByDefault
public class MySQLDataMapper implements DataMapper {
    private MySQLConnectionFactory factory;

    public MySQLDataMapper(MySQLConnectionFactory factory) {
        this.factory = factory;
    }

    /**
     * Load one entity by specifying a SQL query. The class must have a constructor that matches the returned columns
     * by their @Column annotations exactly.
     *
     * @param entityClass
     * @param query
     * @param parameters
     * @param <T>
     *
     * @return
     */
    public <T> T loadOne(Class<T> entityClass, String query, Map<Integer,Object> parameters) {
        List<T> result = load(entityClass, query, parameters);
        if (result.size() == 0) {
            throw new EntityNotFoundException();
        }
        return result.get(0);
    }

    /**
     * Load one entity by specifying a SQL query. The class must have a constructor that matches the returned columns
     * by their @Column annotations exactly.
     *
     * @param entityClass
     * @param query
     * @param parameters
     * @param <T>
     *
     * @return
     */
    public <T> T loadOne(Class<T> entityClass, String query, Object... parameters) {
        List<T> result = load(entityClass, query, parameters);
        if (result.size() == 0) {
            throw new EntityNotFoundException();
        }
        return result.get(0);
    }

    @Override
    public <T> T loadOne(Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadOne(entityClass, parameters);
    }

    @Override
    public <T> T loadOne(Class<T> entityClass, Map<String, Object> parameters) {
        List<T> result = load(entityClass, parameters, 1, null);
        if (result.size() == 0) {
            throw new EntityNotFoundException();
        }
        return result.get(0);
    }

    /**
     * Load a list of entity classes by specifying a SQL query. The class must have a constructor that matches the
     * returned columns by their @Column annotation exactly.
     *
     * @param entityClass
     * @param query
     * @param parameters
     * @param <T>
     *
     * @return
     */
    public <T> List<T> load(Class<T> entityClass, String query, Object... parameters) {
        Map<Integer,Object> newParameters = new HashMap<>();
        int i = 0;
        for (Object parameter : parameters) {
            newParameters.put(i++, parameter);
        }
        return load(entityClass, query, newParameters);
    }

    /**
     * Load a list of entity classes by specifying a SQL query. The class must have a constructor that matches the
     * returned columns by their @Column annotation exactly.
     *
     * @param entityClass
     * @param query
     * @param parameters
     * @param <T>
     *
     * @return
     */
    public <T> List<T> load(Class<T> entityClass, String query, Map<Integer,Object> parameters) {
        BufferedSQLResultTable result = factory.getConnection().query(query, parameters);

        if (result.size() == 0) {
            return new ArrayList<>();
        }

        Constructor<?> validConstructor = null;
        List<String> fieldList = new ArrayList<>();
        for (Constructor<?> constructor : entityClass.getConstructors()) {
            if (constructor.getParameters().length != result.getColumns().size()) {
                continue;
            }
            boolean valid = true;
            List<String> localFieldList = new ArrayList<>();
            for (Parameter parameter : constructor.getParameters()) {
                Column annotation = parameter.getAnnotation(Column.class);
                if (annotation == null) {
                    valid = false;
                    break;
                }
                if (!result.getColumns().containsKey(annotation.value())) {
                    valid = false;
                    break;
                }
                localFieldList.add(annotation.value());
            }
            if (valid) {
                validConstructor = constructor;
                fieldList = localFieldList;
                break;
            }
        }

        if (validConstructor == null) {
            throw new NoValidConstructorFound("No valid constructor found on entity " + entityClass.getName() + " for query " + query);
        }

        List<T> resultList = new ArrayList<>();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < result.size(); i++) {
            int constructorParameterIterator = 0;
            List<Object> constructorParameters = new ArrayList<>();
            int j = 0;
            for (String entry : fieldList) {
                Object value = result.get(i).getField(entry).getValue();
                Parameter parameter = validConstructor.getParameters()[j++];

                if (value != null) {
                    if (value instanceof Timestamp && parameter.getType().equals(Date.class)) {
                        value = new Date(((Timestamp) value).getTime());
                    } else if (value instanceof String && parameter.getType().equals(UUID.class)) {
                        value = UUID.fromString((String) value);
                    } else if (value instanceof Long && parameter.getType().equals(Integer.class)) {
                        value = ((Long) value).intValue();
                    }
                    if (parameter.getType().isEnum()) {
                        //noinspection unchecked,ConstantConditions
                        value = Enum.valueOf((Class<Enum>)parameter.getType(), (String) value);
                    }
                }

                constructorParameters.add(constructorParameterIterator++, value);
            }
            try {
                //noinspection unchecked
                resultList.add((T) validConstructor.newInstance((Object[])constructorParameters.toArray()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new DataMapperException(e);
            }
        }
        return resultList;
    }

    @Override
    public <T> List<T> load(Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return load(entityClass, parameters);
    }

    @Override
    public <T> List<T> load(Class<T> entityClass, Map<String, Object> parameters) {
        return load(entityClass, parameters, null, null);
    }

    @Override
    public <T> List<T> load(
        Class<T> entityClass,
        Map<String, Object> parameters,
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
        sql += "WHERE\n";
        List<String> conditions = new ArrayList<>();
        int i = 0;
        for (String parameter : parameters.keySet()) {
            conditions.add("  " + parameter + "=?\n");
            sqlParameters.put(i++, parameters.get(parameter));
        }
        sql += String.join("  AND\n", conditions);
        if (limit != null) {
            sql += "LIMIT ";
            if (offset != null) {
                sql += offset + ", " + limit;
            } else {
                sql += limit;
            }
        }
        return load(entityClass, sql, sqlParameters);
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
