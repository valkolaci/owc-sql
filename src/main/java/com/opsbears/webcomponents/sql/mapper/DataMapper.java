package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface DataMapper {
    <T> T loadOne(Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> T loadOne(Class<T> entityClass, String query, Object... parameters);
    <T> T loadOne(Class<T> entityClass, String field, Object value);
    <T> T loadOne(Class<T> entityClass, Map<String,Object> parameters);
    <T> List<T> load(Class<T> entityClass, String query, Object... parameters);
    <T> List<T> load(Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> List<T> load(Class<T> entityClass, String field, Object value);
    <T> List<T> load(Class<T> entityClass, Map<String, Object> parameters);
    <T> List<T> load(Class<T> entityClass, Map<String,Object> parameters, @Nullable Integer limit, @Nullable Integer offset);
    void store(Object entity);
}
