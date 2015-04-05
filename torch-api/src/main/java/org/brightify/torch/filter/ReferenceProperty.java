package org.brightify.torch.filter;

import org.brightify.torch.Ref;

/**
 * Property that references another entity.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ReferenceProperty<OWNER, TYPE> extends Property<OWNER, Ref<TYPE>> {

    Class<TYPE> getReferencedType();

}
