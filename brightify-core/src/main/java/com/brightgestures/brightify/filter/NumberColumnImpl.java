package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class NumberColumnImpl<T> extends ColumnImpl<T> implements NumberColumn<T> {

    public NumberColumnImpl(String columName, Class<T> columnType) {
        super(columName, columnType);
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
