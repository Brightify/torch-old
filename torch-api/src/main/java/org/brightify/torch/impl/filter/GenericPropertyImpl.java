package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.GenericProperty;
import org.brightify.torch.filter.InFilter;
import org.brightify.torch.filter.NotInFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class GenericPropertyImpl<TYPE> extends PropertyImpl<TYPE> implements GenericProperty<TYPE> {

    public GenericPropertyImpl(String name, String safeName, Class<TYPE> type, Feature... features) {
        super(name, safeName, type, features);
    }

    @Override
    public InFilter<?> in(TYPE... values) {
        return new InFilter<TYPE>(this, values);
    }

    @Override
    public NotInFilter<?> notIn(TYPE... values) {
        return new NotInFilter<TYPE>(this, values);
    }
}
