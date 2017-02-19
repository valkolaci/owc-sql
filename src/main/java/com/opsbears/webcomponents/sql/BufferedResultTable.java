package com.opsbears.webcomponents.sql;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
class BufferedResultTable implements BufferedSQLResultTable {
    private List<SQLResultRow<BufferedSQLResultColumn>> rows;
    private Map<String, BufferedSQLResultColumn> columns;

    BufferedResultTable(List<BufferedResultRow> rows, Map<String, BufferedResultColumn> columns) {
        this.rows = Collections.unmodifiableList(rows);
        this.columns = Collections.unmodifiableMap(columns);
    }

    @Override
    public BufferedSQLResultColumn getColumnByName(String columnName) {
        return columns.get(columnName);
    }

    @Override
    public SQLResultRow<BufferedSQLResultColumn> getRow(int row) {
        return rows.get(row);
    }

    @Override
    public int size() {
        return rows.size();
    }

    @Override
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return rows.contains(o);
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return rows.toArray();
    }

    @Override
    @Nonnull
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return rows.toArray(a);
    }

    @Override
    public boolean add(SQLResultRow<BufferedSQLResultColumn> sqlResultFields) {
        return rows.add(sqlResultFields);
    }

    @Override
    public boolean remove(Object o) {
        return rows.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return rows.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SQLResultRow<BufferedSQLResultColumn>> c) {
        return rows.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends SQLResultRow<BufferedSQLResultColumn>> c) {
        return rows.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return rows.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super SQLResultRow<BufferedSQLResultColumn>> filter) {
        return rows.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return rows.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<SQLResultRow<BufferedSQLResultColumn>> operator) {
        rows.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super SQLResultRow<BufferedSQLResultColumn>> c) {
        rows.sort(c);
    }

    @Override
    public void clear() {
        rows.clear();
    }

    @Override
    public SQLResultRow<BufferedSQLResultColumn> get(int index) {
        return rows.get(index);
    }

    @Override
    public SQLResultRow<BufferedSQLResultColumn> set(int index, SQLResultRow<BufferedSQLResultColumn> element) {
        return rows.set(index, element);
    }

    @Override
    public void add(int index, SQLResultRow<BufferedSQLResultColumn> element) {
        rows.add(index, element);
    }

    @Override
    public SQLResultRow<BufferedSQLResultColumn> remove(int index) {
        return rows.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return rows.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return rows.lastIndexOf(o);
    }

    @Override
    @Nonnull
    public ListIterator<SQLResultRow<BufferedSQLResultColumn>> listIterator() {
        return rows.listIterator();
    }

    @Override
    @Nonnull
    public ListIterator<SQLResultRow<BufferedSQLResultColumn>> listIterator(int index) {
        return rows.listIterator(index);
    }

    @Override
    @Nonnull
    public List<SQLResultRow<BufferedSQLResultColumn>> subList(int fromIndex, int toIndex) {
        return rows.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<SQLResultRow<BufferedSQLResultColumn>> spliterator() {
        return rows.spliterator();
    }

    @Override
    public Stream<SQLResultRow<BufferedSQLResultColumn>> stream() {
        return rows.stream();
    }

    @Override
    public Stream<SQLResultRow<BufferedSQLResultColumn>> parallelStream() {
        return rows.parallelStream();
    }

    @Override
    @Nonnull
    public Iterator<SQLResultRow<BufferedSQLResultColumn>> iterator() {
        return rows.iterator();
    }

    @Override
    public void forEach(Consumer<? super SQLResultRow<BufferedSQLResultColumn>> action) {
        rows.forEach(action);
    }
}
