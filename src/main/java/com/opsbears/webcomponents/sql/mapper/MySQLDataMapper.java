package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.BufferedSQLResultTable;
import com.opsbears.webcomponents.sql.JDBCMySQLConnectionFactory;
import com.opsbears.webcomponents.sql.MySQLConnectionFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MySQLDataMapper {
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

        Constructor<?> validConstructor = null;
        List<String> fieldList = new ArrayList<>();
        for (Constructor<?> constructor : entityClass.getConstructors()) {
            if (constructor.getParameters().length != result.getColumns().size()) {
                continue;
            }
            List<String> localFieldList = new ArrayList<>();
            for (Parameter parameter : constructor.getParameters()) {
                Column annotation = parameter.getAnnotation(Column.class);
                if (annotation == null) {
                    continue;
                }
                if (!result.getColumns().containsKey(annotation.value())) {
                    continue;
                }
                localFieldList.add(annotation.value());
            }
            validConstructor = constructor;
            fieldList = localFieldList;
            break;
        }

        if (validConstructor == null) {
            throw new NoValidConstructorFound("No valid constructor found on entity " + entityClass.getName() + " for query " + query);
        }

        List<T> resultList = new ArrayList<>();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < result.size(); i++) {
            int constructorParameterIterator = 0;
            List<Object> constructorParameters = new ArrayList<>();
            for (String entry : fieldList) {
                constructorParameters.add(constructorParameterIterator++, result.get(i).getField(entry).getValue());
            }
            try {
                validConstructor.newInstance(constructorParameters.toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new DataMapperException(e);
            }
        }
        return resultList;
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

        String query = "REPLACE INTO " + entity.getClass().getAnnotation(Table.class).value() + " (" +
                       String.join(", ", columns) + ") VALUES (" + String.join(", ", placeholders) + ")";

        factory.getConnection().query(query, values);
    }
}
