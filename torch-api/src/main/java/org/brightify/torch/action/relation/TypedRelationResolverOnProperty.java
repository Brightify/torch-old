package org.brightify.torch.action.relation;

import org.brightify.torch.Key;
import org.brightify.torch.filter.RelationColumn;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedRelationResolverOnProperty<ENTITY, COLUMN extends RelationColumn<ENTITY, VALUE>, VALUE> {

    VALUE parentId(Long id);

    VALUE parent(ENTITY entity);

    VALUE parentKey(Key<ENTITY> key);

}
