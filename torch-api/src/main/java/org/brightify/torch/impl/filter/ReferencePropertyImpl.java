package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.ReferenceProperty;

public class ReferencePropertyImpl<TYPE> extends PropertyImpl<TYPE> implements ReferenceProperty<TYPE> {
    public ReferencePropertyImpl(Class<TYPE> type, String name, String safeName) {
        super(type, name, safeName);
    }
}
