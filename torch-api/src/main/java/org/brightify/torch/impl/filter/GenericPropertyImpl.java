package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.GenericProperty;
import org.brightify.torch.filter.InFilter;
import org.brightify.torch.filter.NotInFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class GenericPropertyImpl<OWNER, TYPE> extends PropertyImpl<OWNER, TYPE>
        implements GenericProperty<OWNER, TYPE> {

    public GenericPropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, type, name, safeName);
    }

    @Override
    public InFilter<OWNER, TYPE> in(TYPE... values) {
        return new InFilter<OWNER, TYPE>(this, values);
    }

    @Override
    public InFilter<OWNER, TYPE> in(Iterable<TYPE> values) {
        return new InFilter<OWNER, TYPE>(this, values);
    }

    @Override
    public NotInFilter<OWNER, TYPE> notIn(TYPE... values) {
        return new NotInFilter<OWNER, TYPE>(this, values);
    }

    @Override
    public NotInFilter<OWNER, TYPE> notIn(Iterable<TYPE> values) {
        return new NotInFilter<OWNER, TYPE>(this, values);
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
