package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.querybuilder.Condition;
import com.opsbears.webcomponents.sql.querybuilder.TableSpec;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface DataMapper {
    <T> T loadOneByQuery(Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> T loadOneByQuery(Class<T> entityClass, String query, Object... parameters);
    <T> T loadOneByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> T loadOneByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Object... parameters);
    <T> T loadOneBy(Class<T> entityClass, String field, Object value);
    <T> T loadOneBy(Class<T> entityClass, Map<String,Object> parameters);
    <T> T loadOneBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value);
    <T> T loadOneBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String,Object> parameters);
    <T> List<T> loadByQuery(Class<T> entityClass, String query, Object... parameters);
    <T> List<T> loadByQuery(Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> List<T> loadByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Object... parameters);
    <T> List<T> loadByQuery(@Nullable Transaction transaction, Class<T> entityClass, String query, Map<Integer,Object> parameters);
    <T> List<T> loadAll(Class<T> entityClass);
    <T> List<T> loadAll(@Nullable Transaction transaction, Class<T> entityClass);
    <T> List<T> loadBy(Class<T> entityClass, String field, Object value);
    <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, String field, Object value, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, Map<String, Object> parameters);
    <T> List<T> loadBy(Class<T> entityClass, Map<String,Object> parameters, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, Map<String, Object> parameters, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String, Object> parameters);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String,Object> parameters, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String, Object> parameters, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, Condition condition);
    <T> List<T> loadBy(Class<T> entityClass, Condition condition, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, TableSpec tableSpec, Condition condition);
    <T> List<T> loadBy(Class<T> entityClass, TableSpec tableSpec, Condition condition, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(Class<T> entityClass, TableSpec tableSpec, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, TableSpec tableSpec, Condition condition);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, TableSpec tableSpec, Condition condition, @Nullable Integer limit, @Nullable Integer offset);
    <T> List<T> loadBy(@Nullable Transaction transaction, Class<T> entityClass, TableSpec tableSpec, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    void store(Object entity);
    void insert(Object entity);
    void update(Object entity);
    void delete(Object entity);
    void store(@Nullable Transaction transaction, Object entity);
    void insert(@Nullable Transaction transaction, Object entity);
    void update(@Nullable Transaction transaction, Object entity);
    void delete(@Nullable Transaction transaction, Object entity);
    <T> void deleteBy(Class<T> entityClass, Condition condition);
    <T> void deleteBy(Class<T> entityClass, Condition condition, @Nullable Integer limit, @Nullable Integer offset);
    <T> void deleteBy(Class<T> entityClass, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> void deleteBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition);
    <T> void deleteBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition, @Nullable Integer limit, @Nullable Integer offset);
    <T> void deleteBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition, @Nullable String orderBy, @Nullable OrderDirection orderDirection, @Nullable Integer limit, @Nullable Integer offset);
    <T> long countBy(Class<T> entityClass, String field, Object value);
    <T> long countBy(Class<T> entityClass, Map<String, Object> parameters);
    <T> long countBy(Class<T> entityClass, Condition condition);
    <T> long countBy(Class<T> entityClass, TableSpec tableSpec, Condition condition);
    <T> long countBy(@Nullable Transaction transaction, Class<T> entityClass, String field, Object value);
    <T> long countBy(@Nullable Transaction transaction, Class<T> entityClass, Map<String, Object> parameters);
    <T> long countBy(@Nullable Transaction transaction, Class<T> entityClass, Condition condition);
    <T> long countBy(@Nullable Transaction transaction, Class<T> entityClass, TableSpec tableSpec, Condition condition);

    enum OrderDirection {
        ASC,
        DESC
    }
}
