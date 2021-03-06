package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.querybuilder.*;
import com.opsbears.webcomponents.typeconverter.SingleTypeConverter;
import com.opsbears.webcomponents.typeconverter.TypeConverterChain;
import com.opsbears.webcomponents.typeconverter.builtin.DefaultTypeConverterChain;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ParametersAreNonnullByDefault
abstract public class AbstractDataMapper implements DataMapper {
    private final TypeConverterChain typeConverter;

    protected AbstractDataMapper() {
        this(DefaultTypeConverterChain.defaultChain(ZoneOffset.UTC));
    }

    protected AbstractDataMapper(TypeConverterChain typeConverter) {
        this.typeConverter = typeConverter;
    }

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
        return loadOneByQuery(null, entityClass, query, parameters);
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
    public <T> T loadOneByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Map<Integer,Object> parameters) {
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
        return loadOneByQuery(null, entityClass, query, parameters);
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
    public <T> T loadOneByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Object... parameters) {
        List<T> result = loadByQuery(entityClass, query, parameters);
        if (result.size() == 0) {
            throw new EntityNotFoundException();
        }
        return result.get(0);
    }

    @Override
    public <T> T loadOneBy(Class<T> entityClass, String field, Object value) {
        return loadOneBy(null, entityClass, field, value);
    }

    @Override
    public <T> T loadOneBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadOneBy(entityClass, parameters);
    }

    @Override
    public <T> T loadOneBy(Class<T> entityClass, Map<String, Object> parameters) {
        return loadOneBy(null, entityClass, parameters);
    }

    @Override
    public <T> T loadOneBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String, Object> parameters) {
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
        return loadByQuery(null, entityClass, query, parameters);
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
    public <T> List<T> loadByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Object... parameters) {
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
        return loadByQuery(null, entityClass, query, parameters);
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
    public <T> List<T> loadByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Map<Integer,Object> parameters) {
        BufferedSQLResultTable result = getConnection().query(transaction, query, parameters);

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
                    value = this.typeConverter.convert(value, parameter.getType());
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
        return loadAll(null, entityClass);
    }

    @Override
    public <T> List<T> loadAll(@Nullable Transaction transaction, Class<T> entityClass) {
        return loadBy(entityClass, new HashMap<>());
    }
    @Override
    public <T> List<T> loadBy(Class<T> entityClass, String field, Object value) {
        return loadBy(null, entityClass, field, value);
    }

    @Override
    public <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadBy(entityClass, parameters);
    }
    @Override
    public <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable Integer limit, @Nullable Integer offset) {
        return loadBy(null, entityClass, field, value, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value, @Nullable Integer limit, @Nullable Integer offset) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadBy(entityClass, parameters, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset) {
        return loadBy(null, entityClass, field, value, orderBy, orderDirection, limit, offset);
    }
    @Override
    public <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return loadBy(entityClass, parameters, orderBy, orderDirection, limit, offset);
    }
    @Override
    public <T> List<T> loadBy(Class<T> entityClass, Map<String, Object> parameters) {
        return loadBy(null, entityClass, parameters);
    }
    @Override
    public <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String, Object> parameters) {
        return loadBy(entityClass, parameters, null, null);
    }

    @Override
    public <T> List<T> loadBy(
        Class<T> entityClass,
        Map<String, Object> parameters,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        return loadBy(null, entityClass, parameters, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
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
            null,
            entityClass,
            condition,
            orderBy,
            orderDirection,
            limit,
            offset
        );
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
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
        return loadBy(null, entityClass, tableSpec, condition, orderBy, orderDirection, limit,offset);
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
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
                columns.add("  " + tableSpec.getRootTableName() + "." + transformColumName(annotation.value()));
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
        return loadBy(null, entityClass, tableSpec, condition);
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
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
        return loadBy(null, entityClass, tableSpec, condition, limit, offset);
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
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
        return loadBy(null, entityClass,parameters,orderBy,orderDirection,limit,offset);
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
        Class<T> entityClass,
        Map<String, Object> parameters,
        @Nullable String orderBy,
        @Nullable OrderDirection orderDirection,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            if (parameter.getValue() == null) {
                conditions.add(new ComparisonCondition(
                    new FieldName(parameter.getKey()),
                    ComparisonCondition.Operator.IS,
                    new com.opsbears.webcomponents.sql.querybuilder.Parameter(null)
                ));
            } else {
                conditions.add(new ComparisonCondition(
                    new FieldName(parameter.getKey()),
                    ComparisonCondition.Operator.EQUALS,
                    new com.opsbears.webcomponents.sql.querybuilder.Parameter(parameter.getValue())
                ));
            }
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
        insert(null, entity);
    }
    @Override
    public void insert(@Nullable Transaction transaction, Object entity) {
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

        try {
            getConnection().query(query, values);
        } catch (com.opsbears.webcomponents.sql.SQLException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new EntityAlreadyExistsException(e);
            }
            throw e;
        }
    }

    public <T> void deleteBy(Class<T> entityClass, Condition condition) {
        deleteBy(null, entityClass, condition);
    }
    public <T> void deleteBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition) {
        deleteBy(entityClass, condition, null, null, null, null);
    }
    public <T> void deleteBy(Class<T> entityClass, Condition condition, @Nullable Integer limit, @Nullable Integer offset) {
        deleteBy(null, entityClass, condition, limit, offset);
    }
    public <T> void deleteBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition, @Nullable Integer limit, @Nullable Integer offset) {
        deleteBy(entityClass, condition, null, null, limit, offset);
    }

    public <T> void deleteBy(Class<T> entityClass, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset) {
        deleteBy(null, entityClass, condition, orderBy, orderDirection, limit, offset);
    }

    public <T> void deleteBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset) {
        if (entityClass.getAnnotation(Table.class) == null) {
            throw new RuntimeException("Missing @Table annotation on " + entityClass.getName());
        }
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        Map<Integer,Object> sqlParameters = new HashMap<>();
        String sql = "DELETE FROM\n";

        sql += tableAnnotation.value() + "\n";
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
        getConnection().query(sql, sqlParameters);
    }

    @Override
    public void delete(Object entity) {
        delete(null, entity);
    }

    @Override
    public void delete(@Nullable Transaction transaction, Object entity) {
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
        update(null, entity);
    }

    @Override
    public void update(@Nullable Transaction transaction, Object entity) {
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
        return countBy(null, entityClass, field, value);
    }

    @Override
    public <T> long countBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(field, value);
        return countBy(entityClass, parameters);
    }

    @Override
    public <T> long countBy(
        Class<T> entityClass,
        Map<String, Object> parameters
    ) {
        return countBy(null, entityClass, parameters);
    }

    @Override
    public <T> long countBy(
        @Nullable Transaction transaction,
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

    public <T> long countBy(
        Class<T> entityClass,
        Condition condition
    ) {
        return countBy(null, entityClass, condition);
    }

    @Override
    public <T> long countBy(
        @Nullable Transaction transaction,
        Class<T> entityClass,
        Condition condition
    ) {
        return countBy(entityClass, new SimpleTableSpec(entityClass.getAnnotation(Table.class).value(), null), condition);
    }

    public <T> long countBy(Class<T> entityClass, TableSpec tableSpec, Condition condition) {
        return countBy(null, entityClass, tableSpec, condition);
    }

    public <T> long countBy(@Nullable Transaction transaction, Class<T> entityClass, TableSpec tableSpec, Condition condition) {
        Map<Integer,Object> sqlParameters = new HashMap<>();
        String sql = "SELECT\n";
        sql += "COUNT(*) cnt\n";
        sql += "FROM\n";
        sql += tableSpec.toString() + "\n";
        if (!condition.getTemplatedQuery().isEmpty()) {
            sql += "WHERE " + condition.getTemplatedQuery() + " ";
            int i = 0;
            for (Object parameterValue : condition.getParameters()) {
                sqlParameters.put(i++, parameterValue);
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
        return loadBy(null, entityClass, condition);
    }

    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
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
        return loadBy(null, entityClass, condition, limit, offset);
    }
    @Override
    public <T> List<T> loadBy(
        @Nullable Transaction transaction,
        Class<T> entityClass,
        Condition condition,
        @Nullable Integer limit,
        @Nullable Integer offset
    ) {
        return this.loadBy(entityClass, condition, null, null, limit, offset);
    }
}
