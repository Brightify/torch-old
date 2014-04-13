package org.brightify.torch.action.relation;

import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.filter.RelationColumn;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class RelationResolverImpl implements RelationResolver {

    private final Torch torch;

    public RelationResolverImpl(Torch torch) {
        this.torch = torch;
    }

    @Override
    public <ENTITY> TypedRelationResolver<ENTITY> with(Class<ENTITY> entityClass) {
        return new TypedRelationResolverImpl<ENTITY>(torch, entityClass);
    }

    private static class TypedRelationResolverImpl<ENTITY> implements TypedRelationResolver<ENTITY> {
        private final Torch torch;
        private final Class<ENTITY> entityClass;

        private TypedRelationResolverImpl(Torch torch, Class<ENTITY> entityClass) {
            this.torch = torch;
            this.entityClass = entityClass;
        }

        @Override
        public <COLUMN extends RelationColumn<ENTITY, VALUE>, VALUE> TypedRelationResolverOnProperty<ENTITY, COLUMN,
                        VALUE> onProperty(RelationColumn<ENTITY, VALUE> value) {
            return null;
        }
    }

    private static class TypedRelationResolverOnPropertyImpl<ENTITY, COLUMN extends RelationColumn<ENTITY, VALUE>, VALUE>
            implements TypedRelationResolverOnProperty<ENTITY, COLUMN, VALUE> {

        @Override
        public VALUE parentId(Long id) {
            return null;
        }

        @Override
        public VALUE parent(ENTITY entity) {
            return null;
        }

        @Override
        public VALUE parentKey(Key<ENTITY> key) {
            return null;
        }
    }

}
