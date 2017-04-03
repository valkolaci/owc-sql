package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface DataMapper {
    <T> T loadOneByQuery(Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> T loadOneByQuery(Class<T> entityClass, String query, Object... parameters);
    <T> T loadOneBy(Class<T> entityClass, String field, Object value);
    <T> T loadOneBy(Class<T> entityClass, Map<String,Object> parameters);
    <T> List<T> loadByQuery(Class<T> entityClass, String query, Object... parameters);
    <T> List<T> loadByQuery(Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> List<T> loadAll(Class<T> entityClass);
    <T> List<T> loadBy(Class<T> entityClass, String field, Object value);
    <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, Map<String, Object> parameters);
    <T> List<T> loadBy(Class<T> entityClass, Map<String,Object> parameters, @Nullable Integer limit, @Nullable Integer offset);
    void store(Object entity);
    void insert(Object entity);
    void update(Object entity);
}
