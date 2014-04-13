package org.brightify.torch.action.relation;

import org.brightify.torch.filter.ListProperty;
import org.brightify.torch.filter.RelationColumn;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedRelationResolver<ENTITY> {

    <VALUE> TypedRelationResolverOnProperty<ENTITY, VALUE> onProperty(ListProperty<ENTITY, VALUE> value);

}
