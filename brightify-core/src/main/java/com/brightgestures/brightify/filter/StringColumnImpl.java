package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class StringColumnImpl extends ColumnImpl<String> implements StringColumn {

    public StringColumnImpl(String columName) {
        super(columName, String.class);
    }

    @Override
    public EntityFilter startsWith(String value) {
        // FIXME add escaping of input value!
        return EntityFilter.filter(getName() + " LIKE ?", value + "%");
    }

    @Override
    public EntityFilter endsWith(String value) {
        return EntityFilter.filter(getName() + " LIKE ?", "%" + value);
    }

    @Override
    public EntityFilter contains(String value) {
        return EntityFilter.filter(getName() + " LIKE ?", "%" + value + "%");
    }
}
