package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.querybuilder.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@ParametersAreNonnullByDefault
abstract public class AbstractDataMapper implements DataMapper {
    abstract protected BufferedSQLDatabaseConnection getConnection();

    protected String transformColumName(String columnName) {
        return columnName;
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
    public <T> T loadOneByQuery(Class<T> entityClass, String query, Map<Integer,Object> parameters) {
        List<T> result = loadByQuery(entityClass, query, parameters);
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
    public <T> T loadOneByQuery(Class<T> entityClass, String query, Object... parameters) {
        List<T> result = loadByQuery(entityClass, query, parameters);
        if (result.size() == 0) {
            throw new EntityNotFoundException();
        }
        return result.get(0);
    }

    @Override
    public <T> T loadOneBy(Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadOneBy(entityClass, parameters);
    }

    @Override
    public <T> T loadOneBy(Class<T> entityClass, Map<String, Object> parameters) {
        List<T> result = loadBy(entityClass, parameters, 1, null);
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
    public <T> List<T> loadByQuery(Class<T> entityClass, String query, Object... parameters) {
        Map<Integer,Object> newParameters = new HashMap<>();
        int i = 0;
        for (Object parameter : parameters) {
            newParameters.put(i++, parameter);
        }
        return loadByQuery(entityClass, query, newParameters);
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
    public <T> List<T> loadByQuery(Class<T> entityClass, String query, Map<Integer,Object> parameters) {
        BufferedSQLResultTable result = getConnection().query(query, parameters);

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
                Column columnAnnotation = parameter.getAnnotation(Column.class);
                if (columnAnnotation != null) {
                    if (!result.getColumns().containsKey(transformColumName(columnAnnotation.value()))) {
                        valid = false;
                        break;
                    }
                    localFieldList.add(columnAnnotation.value());
                } else {
                    valid = false;
                    break;
                }
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
                Object value = result.get(i).getField(transformColumName(entry)).getValue();
                Parameter parameter = validConstructor.getParameters()[j++];

                if (value != null) {
                    if (value instanceof Timestamp && parameter.getType().equals(Date.class)) {
                        value = new Date(((Timestamp) value).getTime());
                    } else if (value instanceof Timestamp && parameter.getType().equals(LocalDateTime.class)) {
                        value = ((Timestamp) value).toLocalDateTime();
                    } else if (value instanceof String && parameter.getType().equals(UUID.class)) {
                        value = UUID.fromString((String) value);
                    } else if (value instanceof Long && parameter.getType().equals(Integer.class)) {
                        value = ((Long) value).intValue();
                    }
                    if (parameter.getType().isEnum()) {
                        //noinspection unchecked,ConstantConditions
                        try {
                            Enum[] values = (Enum[]) parameter.getType().getMethod("values").invoke(null);
                            for (Enum enumValue : values) {
                                if (enumValue.toString().equals(value)) {
                                    value = enumValue;
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
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
    public <T> List<T> loadAll(Class<T> entityClass) {
        return loadBy(entityClass, new HashMap<>());
    }

    @Override
    public <T> List<T> loadBy(Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadBy(entityClass, parameters);
    }

    @Override
    public <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable Integer limit, @Nullable Integer offset) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadBy(entityClass, parameters, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadBy(entityClass, parameters, orderBy, orderDirection, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(Class<T> entityClass, Map<String, Object> parameters) {
        return loadBy(entityClass, parameters, null, null);
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        Map<String, Object> parameters,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        return loadBy(entityClass, parameters, null, null, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        Condition condition,
        @Nullable String orderBy,
        @Nullable OrderDirection orderDirection,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        return loadBy(
            entityClass,
            new SimpleTableSpec(
                entityClass.getAnnotation(Table.class).value(),
                null
            ),
            condition,
            orderBy,
            orderDirection,
            limit,
            offset
        );
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        TableSpec tableSpec,
        Condition condition,
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
                columns.add("  " + tableSpec.getRootTableName() + "." + annotation.value().toUpperCase());
            }
        }
        sql += String.join(",\n", columns) + "\n";
        sql += "FROM\n";
        sql += tableSpec.toString() + "\n";
        if (!condition.getTemplatedQuery().isEmpty()) {
            sql += "WHERE " + condition.getTemplatedQuery() + " ";
            int i = 0;
            for (Object parameterValue : condition.getParameters()) {
                sqlParameters.put(i++, parameterValue);
            }
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
    public <T> List<T> loadBy(
        Class<T> entityClass,
        TableSpec tableSpec,
        Condition condition
    ) {
        return loadBy(
            entityClass,
            tableSpec,
            condition,
            null,
            null,
            null,
            null
        );
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        TableSpec tableSpec,
        Condition condition,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        return loadBy(
            entityClass,
            tableSpec,
            condition,
            null,
            null,
            limit,
            offset
        );
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
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            conditions.add(new ComparisonCondition(
                new FieldName(parameter.getKey()),
                ComparisonCondition.Operator.EQUALS,
                new com.opsbears.webcomponents.sql.querybuilder.Parameter(parameter.getValue())
            ));
        }
        return loadBy(
            entityClass,
            new LogicalConditionList(LogicalConditionList.Type.AND, conditions.toArray(new Condition[0])),
            orderBy,
            orderDirection,
            limit,
            offset
        );
    }

    @Override
    public void insert(Object entity) {
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

        String query =
            "INSERT INTO " + entity.getClass().getAnnotation(Table.class).value() + " (" +
                String.join(", ", columns) +
                ") VALUES (" +
                String.join(", ", placeholders) +
                ")";

        getConnection().query(query, values);
    }

    @Override
    public void delete(Object entity) {
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new RuntimeException("Missing @Table annotation on " + entity.getClass().getName());
        }

        Map<Integer,Object> values = new HashMap<>();

        List<String> wherePlaceholder = new ArrayList<>();

        int i = 0;
        for (Method method : entity.getClass().getMethods()) {
            Primary primaryAnnotation = method.getAnnotation(Primary.class);
            Column columnAnnotation = method.getAnnotation(Column.class);
            if (primaryAnnotation != null) {
                if (columnAnnotation == null) {
                    throw new InvalidAnnotationException("@Primary always has to be used in conjunction with @Column");
                }

                wherePlaceholder.add(columnAnnotation.value() + "=?");
                try {
                    values.put(i++, method.invoke(entity));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                        "Failed to fetch column value from " +
                            entity.getClass().getName() +
                            "." +
                            method.getName() +
                            "()"
                    );
                }
            }
        }

        String query =
            "DELETE FROM " +
                entity.getClass().getAnnotation(Table.class).value() +
                " WHERE " + String.join(" AND ", wherePlaceholder);

        getConnection().query(query, values);
    }

    @Override
    public void update(Object entity) {
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new RuntimeException("Missing @Table annotation on " + entity.getClass().getName());
        }

        Map<Integer,Object> values = new HashMap<>();

        List<String> wherePlaceholder = new ArrayList<>();

        int i = 0;
        List<String> updatePlaceholder = new ArrayList<>();
        for (Method method : entity.getClass().getMethods()) {
            Column columnAnnotation = method.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                try {
                    updatePlaceholder.add(columnAnnotation.value() + "=?");
                    values.put(i++, method.invoke(entity));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                        "Failed to fetch column value from " +
                            entity.getClass().getName() +
                            "." +
                            method.getName() +
                            "()"
                    );
                }
            }
        }

        for (Method method : entity.getClass().getMethods()) {
            Primary primaryAnnotation = method.getAnnotation(Primary.class);
            Column columnAnnotation = method.getAnnotation(Column.class);
            if (primaryAnnotation != null) {
                if (columnAnnotation == null) {
                    throw new InvalidAnnotationException("@Primary always has to be used in conjunction with @Column");
                }

                wherePlaceholder.add(columnAnnotation.value() + "=?");
                try {
                    values.put(i++, method.invoke(entity));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                        "Failed to fetch column value from " +
                            entity.getClass().getName() +
                            "." +
                            method.getName() +
                            "()"
                    );
                }
            }
        }

        String query =
            "UPDATE " +
                entity.getClass().getAnnotation(Table.class).value() +
                " SET " + String.join(", ", updatePlaceholder) +
                " WHERE " + String.join(" AND ", wherePlaceholder);

        getConnection().query(query, values);
    }

    @Override
    public <T> long countBy(Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return countBy(entityClass, parameters);
    }

    @Override
    public <T> long countBy(
        Class<T> entityClass,
        Map<String, Object> parameters
    ) {
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            conditions.add(new ComparisonCondition(
                new FieldName(parameter.getKey()),
                ComparisonCondition.Operator.EQUALS,
                new com.opsbears.webcomponents.sql.querybuilder.Parameter(parameter.getValue())
            ));
        }
        return countBy(
            entityClass,
            new LogicalConditionList(LogicalConditionList.Type.AND, conditions.toArray(new Condition[0]))
        );
    }

    @Override
    public <T> long countBy(
        Class<T> entityClass,
        Condition condition
    ) {
        return countBy(entityClass, new SimpleTableSpec(entityClass.getAnnotation(Table.class).value(), null), condition);
    }

    public <T> long countBy(Class<T> entityClass, TableSpec tableSpec, Condition condition) {
        Map<Integer,Object> sqlParameters = new HashMap<>();
        String sql = "SELECT\n";
        sql += "COUNT(*) cnt\n";
        sql += "FROM\n";
        sql += tableSpec.toString() + "\n";
        if (!condition.getTemplatedQuery().isEmpty()) {
            sql += "WHERE " + condition.getTemplatedQuery() + " ";
            int i = 0;
            for (Object parameterValue : condition.getParameters()) {
                sqlParameters.put(i, parameterValue);
            }
        }

        Object value = getConnection().query(sql, sqlParameters).getRow(0).getField(transformColumName("cnt")).getValue();
        if (value instanceof Integer) {
            return (((Integer) value).longValue());
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Short) {
            return ((Short) value).longValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger)value).longValue();
        } else {
            throw new RuntimeException("Unexpected return type for count");
        }
    }


    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        Condition condition
    ) {
        return this.loadBy(entityClass, condition, null, null, null, null);
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        Condition condition,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        return this.loadBy(entityClass, condition, null, null, limit, offset);
    }
}
