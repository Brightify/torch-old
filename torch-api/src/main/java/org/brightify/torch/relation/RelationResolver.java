package org.brightify.torch.relation;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface RelationResolver {

    <ENTITY> TypedRelationResolver<ENTITY> with(Class<ENTITY> entityClass);

}
