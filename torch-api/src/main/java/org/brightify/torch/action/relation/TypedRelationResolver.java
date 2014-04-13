package org.brightify.torch.action.relation;

import org.brightify.torch.filter.RelationColumn;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedRelationResolver<ENTITY> {

    <COLUMN extends RelationColumn<ENTITY, VALUE>, VALUE>
    TypedRelationResolverOnProperty<ENTITY, COLUMN, VALUE> onProperty(RelationColumn<ENTITY, VALUE> value);

}
