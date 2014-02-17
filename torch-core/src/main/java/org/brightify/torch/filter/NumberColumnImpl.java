package org.brightify.torch.filter;

import org.brightify.torch.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class NumberColumnImpl<T> extends GenericColumnImpl<T> implements NumberColumn<T> {

    public NumberColumnImpl(String columnName) {
        super(columnName);
    }

    @Override
    public EntityFilter greaterThan(T value) {
        return EntityFilter.filter(getName() + " > ?", value);
    }

    @Override
    public EntityFilter lessThan(T value) {
        return EntityFilter.filter(getName() + " < ?", value);
    }

    @Override
    public EntityFilter greaterThanOrEqualTo(T value) {
        return EntityFilter.filter(getName() + " >= ?", value);
    }

    @Override
    public EntityFilter lessThanOrEqualTo(T value) {
        return EntityFilter.filter(getName() + " <= ?", value);
    }
}
