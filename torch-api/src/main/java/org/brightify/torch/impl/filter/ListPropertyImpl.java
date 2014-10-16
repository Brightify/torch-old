package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.EntityFilter;
import org.brightify.torch.filter.ListProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ListPropertyImpl<PARENT, T> extends PropertyImpl<T> implements ListProperty<PARENT, T> {

    public ListPropertyImpl(String name, String safeName, Class<T> type, Feature... features) {
        super(name, safeName, type, features);
    }

    @Override
    public EntityFilter contains(T... values) {
        return null;
    }

    @Override
    public EntityFilter excludes(T... values) {
        return null;
    }
}
