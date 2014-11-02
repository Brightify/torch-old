package org.brightify.torch.relation;

import org.brightify.torch.Key;
import org.brightify.torch.util.LazyArrayList;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedRelationResolverOnProperty<ENTITY, VALUE> {

    LazyArrayList<VALUE> parentId(Long id);

    LazyArrayList<VALUE> parent(ENTITY entity);

    LazyArrayList<VALUE> parentKey(Key<ENTITY> key);

}
