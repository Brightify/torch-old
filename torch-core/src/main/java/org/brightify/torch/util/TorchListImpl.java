package org.brightify.torch.util;

import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Torch;
import org.brightify.torch.TorchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchListImpl<BACKING_ENTITY extends KeyValueEntity<Long, E>, E> implements TorchList<BACKING_ENTITY, E> {

    private final EntityMetadata<BACKING_ENTITY> metadata;
    private final ArrayList<BACKING_ENTITY> entities;

    public TorchListImpl(Class<BACKING_ENTITY> backingEntityClass, List<E> values) {
        this.metadata = TorchService.factory().getEntities().getMetadata(backingEntityClass);
        this.entities = new ArrayList<BACKING_ENTITY>();
    }

    @Override
    public void save(Torch torch) {

    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public boolean isEmpty() {
        return entities.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }
}
