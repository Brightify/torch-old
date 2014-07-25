package org.brightify.torch.action.relation;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.filter.ListProperty;
import org.brightify.torch.relation.RelationResolver;
import org.brightify.torch.relation.TypedRelationResolver;
import org.brightify.torch.relation.TypedRelationResolverOnProperty;
import org.brightify.torch.util.LazyArrayList;

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
        public <VALUE> TypedRelationResolverOnProperty<ENTITY, VALUE> onProperty(ListProperty<ENTITY, VALUE> property) {
            return new TypedRelationResolverOnPropertyImpl<ENTITY, VALUE>(torch, entityClass, property);
        }
    }

    private static class TypedRelationResolverOnPropertyImpl<ENTITY, VALUE>
            implements TypedRelationResolverOnProperty<ENTITY, VALUE> {

        private final Torch torch;
        private final Class<ENTITY> entityClass;
        private final ListProperty<ENTITY, VALUE> property;
        private final EntityDescription<ENTITY> metadata;

        private TypedRelationResolverOnPropertyImpl(Torch torch, Class<ENTITY> entityClass,
                                                    ListProperty<ENTITY, VALUE> property) {
            this.torch = torch;
            this.entityClass = entityClass;
            this.property = property;
            this.metadata = torch.getFactory().getEntities().getDescription(entityClass);
        }

        @Override
        public LazyArrayList<VALUE> parentId(Long id) {
            String sql = "SELECT * FROM ... WHERE ";


            return null;
        }

        @Override
        public LazyArrayList<VALUE> parent(ENTITY entity) {
            return parentId(metadata.getEntityId(entity));
        }

        @Override
        public LazyArrayList<VALUE> parentKey(Key<ENTITY> key) {
            return parentId(key.getId());
        }
    }

}
