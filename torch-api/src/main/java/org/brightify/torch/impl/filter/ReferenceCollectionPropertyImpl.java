package org.brightify.torch.impl.filter;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.filter.ReferenceCollectionProperty;
import org.brightify.torch.util.Unsafe;

import java.util.Collection;

public abstract class ReferenceCollectionPropertyImpl<OWNER, TYPE> extends PropertyImpl<OWNER, Collection<TYPE>>
        implements ReferenceCollectionProperty<OWNER, TYPE> {

    private final Class<TYPE> referencedType;
    private EntityDescription<TYPE> referencedEntityDescription;

    public ReferenceCollectionPropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, Unsafe.<Class<Collection<TYPE>>>cast(Collection.class), name, safeName);
        this.referencedType = type;
    }

    @Override
    public Class<TYPE> getReferencedType() {
        return referencedType;
    }

    @Override
    public EntityDescription<TYPE> getReferencedEntityDescription() {
        return referencedEntityDescription;
    }

    @Override
    public void setReferencedEntityDescription(EntityDescription<TYPE> description) {
        this.referencedEntityDescription = description;
    }

    @Override
    public ReferenceCollectionPropertyImpl<OWNER, TYPE> defaultValue(Collection<TYPE> defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public ReferenceCollectionPropertyImpl<OWNER, TYPE> feature(Feature feature) {
        super.feature(feature);
        return this;
    }

}
