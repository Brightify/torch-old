package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.EntityFilter;
import org.brightify.torch.filter.ListProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ListPropertyImpl<PARENT, T> extends PropertyImpl<T> implements ListProperty<PARENT, T> {

    public ListPropertyImpl(Class<T> type, String name, String safeName) {
        super(type, name, safeName);
    }

    @Override
    public EntityFilter contains(T... values) {
        return null;
    }

    @Override
    public EntityFilter excludes(T... values) {
        return null;
    }

    @Override
    public ListPropertyImpl<PARENT, T> defaultValue(T defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public ListPropertyImpl<PARENT, T> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
