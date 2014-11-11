package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.GreaterThanFilter;
import org.brightify.torch.filter.GreaterThanOrEqualToFilter;
import org.brightify.torch.filter.LessThanFilter;
import org.brightify.torch.filter.LessThanOrEqualToFilter;
import org.brightify.torch.filter.NumberProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class NumberPropertyImpl<TYPE extends Number> extends GenericPropertyImpl<TYPE> implements NumberProperty<TYPE> {

    public NumberPropertyImpl(Class<TYPE> type, String name, String safeName) {
        super(type, name, safeName);
    }

    @Override
    public GreaterThanFilter<?> greaterThan(TYPE value) {
        return new GreaterThanFilter<TYPE>(this, value);
    }

    @Override
    public LessThanFilter<?> lessThan(TYPE value) {
        return new LessThanFilter<TYPE>(this, value);
    }

    @Override
    public GreaterThanOrEqualToFilter<?> greaterThanOrEqualTo(TYPE value) {
        return new GreaterThanOrEqualToFilter<TYPE>(this, value);
    }

    @Override
    public LessThanOrEqualToFilter<?> lessThanOrEqualTo(TYPE value) {
        return new LessThanOrEqualToFilter<TYPE>(this, value);
    }

    @Override
    public NumberPropertyImpl<TYPE> defaultValue(TYPE defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public NumberPropertyImpl<TYPE> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
