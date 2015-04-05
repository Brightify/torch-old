package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.EntityFilter;
import org.brightify.torch.filter.ListProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class ListPropertyImpl<OWNER, TYPE> extends PropertyImpl<OWNER, TYPE>
        implements ListProperty<OWNER, TYPE> {

    public ListPropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, type, name, safeName);
    }

    @Override
    public EntityFilter contains(TYPE... values) {
        return null;
    }

    @Override
    public EntityFilter excludes(TYPE... values) {
        return null;
    }

}
