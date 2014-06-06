package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ListPropertyImpl<PARENT, T> extends PropertyImpl<T> implements ListProperty<PARENT, T> {

    public ListPropertyImpl(String columnName) {
        super(columnName);
    }

    @Override
    public EntityFilter contains(T... values) {
        return null;
    }

    @Override
    public EntityFilter excludes(T... values) {
        return null;
    }
}
