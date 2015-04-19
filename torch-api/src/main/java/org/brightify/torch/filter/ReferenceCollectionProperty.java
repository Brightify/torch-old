package org.brightify.torch.filter;

import org.brightify.torch.EntityDescription;

import java.util.Collection;

/**
 * Property that references another entity.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ReferenceCollectionProperty<OWNER, TYPE> extends Property<OWNER, Collection<TYPE>> {

    Class<TYPE> getReferencedType();

    EntityDescription<TYPE> getReferencedEntityDescription();

    void setReferencedEntityDescription(EntityDescription<TYPE> description);

}
