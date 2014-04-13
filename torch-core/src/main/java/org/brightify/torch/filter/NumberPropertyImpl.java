package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class NumberPropertyImpl<T> extends GenericPropertyImpl<T> implements NumberProperty<T> {

    public NumberPropertyImpl(String columnName) {
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
