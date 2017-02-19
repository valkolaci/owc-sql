package com.opsbears.webcomponents.sql;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
class BufferedResultColumn extends ResultColumn implements BufferedSQLResultColumn<BufferedResultField> {
    private List<BufferedResultField> fields = new ArrayList<>();

    BufferedResultColumn(String name, List<BufferedResultField> fields) {
        super(name);
        for (BufferedResultField field : fields) {
            field.linkColumn(this);
        }
        this.fields = Collections.unmodifiableList(fields);
    }

    @Override
    public int size() {
        return fields.size();
    }

    @Override
    public boolean isEmpty() {
        return fields.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return fields.contains(o);
    }

    @Override
    @Nonnull
    public Iterator<BufferedResultField> iterator() {
        return fields.iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return fields.toArray();
    }

    @Override
    @Nonnull
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return fields.toArray(a);
    }

    @Override
    public boolean add(BufferedResultField bufferedResultField) {
        return fields.add(bufferedResultField);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return fields.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends BufferedResultField> c) {
        return fields.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends BufferedResultField> c) {
        return fields.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return fields.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super BufferedResultField> filter) {
        return fields.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return fields.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<BufferedResultField> operator) {
        fields.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super BufferedResultField> c) {
        fields.sort(c);
    }

    @Override
    public void clear() {
        fields.clear();
    }

    @Override
    public BufferedResultField get(int index) {
        return fields.get(index);
    }

    @Override
    public BufferedResultField set(int index, BufferedResultField element) {
        return fields.set(index, element);
    }

    @Override
    public void add(int index, BufferedResultField element) {
        fields.add(index, element);
    }

    @Override
    public BufferedResultField remove(int index) {
        return fields.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return fields.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return fields.lastIndexOf(o);
    }

    @Override
    @Nonnull
    public ListIterator<BufferedResultField> listIterator() {
        return fields.listIterator();
    }

    @Override
    @Nonnull
    public ListIterator<BufferedResultField> listIterator(int index) {
        return fields.listIterator(index);
    }

    @Override
    @Nonnull
    public List<BufferedResultField> subList(int fromIndex, int toIndex) {
        return fields.subList(fromIndex, toIndex);
    }

    @Override
    public void forEach(Consumer<? super BufferedResultField> action) {
        fields.forEach(action);
    }

    @Override
    public Spliterator<BufferedResultField> spliterator() {
        return fields.spliterator();
    }

    @Override
    public Stream<BufferedResultField> stream() {
        return fields.stream();
    }

    @Override
    public Stream<BufferedResultField> parallelStream() {
        return fields.parallelStream();
    }
}
