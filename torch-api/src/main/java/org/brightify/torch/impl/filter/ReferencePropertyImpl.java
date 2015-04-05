package org.brightify.torch.impl.filter;

import org.brightify.torch.Ref;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.util.Unsafe;

public abstract class ReferencePropertyImpl<OWNER, TYPE> extends PropertyImpl<OWNER, Ref<TYPE>>
        implements ReferenceProperty<OWNER, TYPE> {

    private final Class<TYPE> referredType;

    public ReferencePropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, Unsafe.<Class<Ref<TYPE>>>cast(Ref.class), name, safeName);
        this.referredType = type;
    }

    @Override
    public Class<TYPE> getReferencedType() {
        return referredType;
    }

    @Override
    public ReferencePropertyImpl<OWNER, TYPE> defaultValue(Ref<TYPE> defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public ReferencePropertyImpl<OWNER, TYPE> feature(Feature feature) {
        super.feature(feature);
        return this;
    }

}
