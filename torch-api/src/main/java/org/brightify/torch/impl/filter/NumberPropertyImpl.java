package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.SingleValueFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class NumberPropertyImpl<OWNER, TYPE extends Number> extends GenericPropertyImpl<OWNER, TYPE>
        implements NumberProperty<OWNER, TYPE> {

    public NumberPropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, type, name, safeName);
    }

    @Override
    public BaseFilter<OWNER, TYPE> greaterThan(TYPE value) {
        return new SingleValueFilter<OWNER, TYPE>(this, BaseFilter.FilterType.GREATER_THAN, value);
    }

    @Override
    public BaseFilter<OWNER, TYPE> lessThan(TYPE value) {
        return new SingleValueFilter<OWNER, TYPE>(this, BaseFilter.FilterType.LESS_THAN, value);
    }

    @Override
    public BaseFilter<OWNER, TYPE> greaterThanOrEqualTo(TYPE value) {
        return new SingleValueFilter<OWNER, TYPE>(this, BaseFilter.FilterType.GREATER_THAN_OR_EQUAL_TO, value);
    }

    @Override
    public BaseFilter<OWNER, TYPE> lessThanOrEqualTo(TYPE value) {
        return new SingleValueFilter<OWNER, TYPE>(this, BaseFilter.FilterType.LESS_THAN_OR_EQUAL_TO, value);
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
