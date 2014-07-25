package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class NumberPropertyImpl<T> extends GenericPropertyImpl<T> implements NumberProperty<T> {

    public NumberPropertyImpl(String name, String safeName, Class<T> type, Feature... features) {
        super(name, safeName, type, features);
    }

    @Override
    public EntityFilter greaterThan(T value) {
        return EntityFilter.filter(getSafeName() + " > ?", value);
    }

    @Override
    public EntityFilter lessThan(T value) {
        return EntityFilter.filter(getSafeName() + " < ?", value);
    }

    @Override
    public EntityFilter greaterThanOrEqualTo(T value) {
        return EntityFilter.filter(getSafeName() + " >= ?", value);
    }

    @Override
    public EntityFilter lessThanOrEqualTo(T value) {
        return EntityFilter.filter(getSafeName() + " <= ?", value);
    }
}
