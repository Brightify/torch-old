package org.brightify.torch.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ArrayListBuilder<T> {

    private final ArrayList<T> list;

    public ArrayListBuilder() {
        list = new ArrayList<T>();
    }

    public ArrayListBuilder(int initialCapacity) {
        list = new ArrayList<T>(initialCapacity);
    }

    public ArrayListBuilder(Collection<? extends T> fromCollection) {
        list = new ArrayList<T>(fromCollection);
    }

    public ArrayList<T> arrayList() {
        return list;
    }

    public List<T> list() {
        return list;
    }

    public List<T> unmofifiableList() {
        return Collections.unmodifiableList(list);
    }

    public ArrayListBuilder<T> add(T item) {
        list.add(item);
        return this;
    }

    public static <T> ArrayListBuilder<T> from(Collection<? extends T> collection) {
        return new ArrayListBuilder<T>(collection);
    }

    public static <T> ArrayListBuilder<T> begin() {
        return new ArrayListBuilder<T>();
    }

    public static <T> ArrayListBuilder<T> expectSize(int size) {
        return new ArrayListBuilder<T>(size);
    }

}
