package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.EnumerationFilter;
import org.brightify.torch.filter.GenericProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class GenericPropertyImpl<OWNER, TYPE> extends PropertyImpl<OWNER, TYPE>
        implements GenericProperty<OWNER, TYPE> {

    public GenericPropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, type, name, safeName);
    }

    @Override
    public BaseFilter<OWNER, TYPE> in(TYPE... values) {
        return new EnumerationFilter<OWNER, TYPE>(this, BaseFilter.FilterType.IN, values);
    }

    @Override
    public BaseFilter<OWNER, TYPE> in(Iterable<TYPE> values) {
        return new EnumerationFilter<OWNER, TYPE>(this, BaseFilter.FilterType.IN, values);
    }

    @Override
    public BaseFilter<OWNER, TYPE> notIn(TYPE... values) {
        return new EnumerationFilter<OWNER, TYPE>(this, BaseFilter.FilterType.NOT_IN, values);
    }

    @Override
    public BaseFilter<OWNER, TYPE> notIn(Iterable<TYPE> values) {
        return new EnumerationFilter<OWNER, TYPE>(this, BaseFilter.FilterType.NOT_IN, values);
    }

    @Override
    public GenericPropertyImpl<OWNER, TYPE> defaultValue(TYPE defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public GenericPropertyImpl<OWNER, TYPE> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
