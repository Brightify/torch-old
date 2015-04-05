package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.GreaterThanFilter;
import org.brightify.torch.filter.GreaterThanOrEqualToFilter;
import org.brightify.torch.filter.LessThanFilter;
import org.brightify.torch.filter.LessThanOrEqualToFilter;
import org.brightify.torch.filter.NumberProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class NumberPropertyImpl<OWNER, TYPE extends Number> extends GenericPropertyImpl<OWNER, TYPE>
        implements NumberProperty<OWNER, TYPE> {

    public NumberPropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, type, name, safeName);
    }

    @Override
    public GreaterThanFilter<OWNER, TYPE> greaterThan(TYPE value) {
        return new GreaterThanFilter<OWNER, TYPE>(this, value);
    }

    @Override
    public LessThanFilter<OWNER, TYPE> lessThan(TYPE value) {
        return new LessThanFilter<OWNER, TYPE>(this, value);
    }

    @Override
    public GreaterThanOrEqualToFilter<OWNER, TYPE> greaterThanOrEqualTo(TYPE value) {
        return new GreaterThanOrEqualToFilter<OWNER, TYPE>(this, value);
    }

    @Override
    public LessThanOrEqualToFilter<OWNER, TYPE> lessThanOrEqualTo(TYPE value) {
        return new LessThanOrEqualToFilter<OWNER, TYPE>(this, value);
    }

    @Override
    public NumberPropertyImpl<OWNER, TYPE> defaultValue(TYPE defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public NumberPropertyImpl<OWNER, TYPE> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
