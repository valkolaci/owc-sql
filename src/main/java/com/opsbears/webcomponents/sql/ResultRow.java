package com.opsbears.webcomponents.sql;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
abstract class ResultRow<TColumnType extends SQLResultColumn> implements SQLResultRow<TColumnType> {
    private Map<String,SQLResultField<TColumnType>> fields;

    ResultRow(Map<String, SQLResultField<TColumnType>> fields) {
        this.fields = Collections.unmodifiableMap(fields);
    }

    @Override
    public SQLResultField<TColumnType> getField(String field) {
        return this.fields.get(field);
    }

    @Override
    public Iterator<SQLResultField<TColumnType>> iterator() {
        return this.fields.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super SQLResultField<TColumnType>> action) {
        this.fields.values().forEach(action);
    }

    @Override
    public Spliterator<SQLResultField<TColumnType>> spliterator() {
        return this.fields.values().spliterator();
    }

    @Override
    public int size() {
        return this.fields.size();
    }

    @Override
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.fields.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        //noinspection SuspiciousMethodCalls
        return this.fields.containsKey(value);
    }

    @Override
    public SQLResultField<TColumnType> get(Object key) {
        return this.fields.get(key);
    }

    @Override
    public SQLResultField<TColumnType> put(String key, SQLResultField<TColumnType> value) {
        return this.fields.put(key, value);
    }

    @Override
    public SQLResultField<TColumnType> remove(Object key) {
        return this.fields.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends SQLResultField<TColumnType>> m) {
        this.fields.putAll(m);
    }

    @Override
    public void clear() {
        this.fields.clear();
    }

    @Override
    @Nonnull
    public Set<String> keySet() {
        return this.fields.keySet();
    }

    @Override
    @Nonnull
    public Collection<SQLResultField<TColumnType>> values() {
        return this.fields.values();
    }

    @Override
    @Nonnull
    public Set<Entry<String, SQLResultField<TColumnType>>> entrySet() {
        return this.fields.entrySet();
    }

    @Override
    public SQLResultField<TColumnType> getOrDefault(Object key, SQLResultField<TColumnType> defaultValue) {
        return this.fields.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super SQLResultField<TColumnType>> action) {
        this.fields.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super SQLResultField<TColumnType>, ? extends SQLResultField<TColumnType>> function) {
        this.fields.replaceAll(function);
    }

    @Override
    public SQLResultField<TColumnType> putIfAbsent(String key, SQLResultField<TColumnType> value) {
        return this.fields.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.fields.remove(key, value);
    }

    @Override
    public boolean replace(String key, SQLResultField<TColumnType> oldValue, SQLResultField<TColumnType> newValue) {
        return this.fields.replace(key, oldValue, newValue);
    }

    @Override
    public SQLResultField<TColumnType> replace(String key, SQLResultField<TColumnType> value) {
        return this.fields.replace(key, value);
    }

    @Override
    public SQLResultField<TColumnType> computeIfAbsent(String key, Function<? super String, ? extends SQLResultField<TColumnType>> mappingFunction) {
        return this.fields.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public SQLResultField<TColumnType> computeIfPresent(String key, BiFunction<? super String, ? super SQLResultField<TColumnType>, ? extends SQLResultField<TColumnType>> remappingFunction) {
        return this.fields.computeIfPresent(key, remappingFunction);
    }

    @Override
    public SQLResultField<TColumnType> compute(String key, BiFunction<? super String, ? super SQLResultField<TColumnType>, ? extends SQLResultField<TColumnType>> remappingFunction) {
        return this.fields.compute(key, remappingFunction);
    }

    @Override
    public SQLResultField<TColumnType> merge(String key, SQLResultField<TColumnType> value, BiFunction<? super SQLResultField<TColumnType>, ? super SQLResultField<TColumnType>, ? extends SQLResultField<TColumnType>> remappingFunction) {
        return this.fields.merge(key, value, remappingFunction);
    }
}
