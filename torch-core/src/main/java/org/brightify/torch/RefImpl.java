package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class RefImpl<ENTITY> implements Ref<ENTITY> {
    private final EntityMetadata<ENTITY> metadata;
    private final Class<ENTITY> entityClass;
    private final ENTITY entity;

    public RefImpl(EntityMetadata<ENTITY> metadata, ENTITY entity) {
        this.metadata = metadata;
        this.entityClass = metadata.getEntityClass();
        this.entity = entity;
    }

    @Override
    public ENTITY get() {
        return entity;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean isSaved() {
        return getEntityId() != null;
    }

    @Override
    public Class<ENTITY> getEntityClass() {
        return entityClass;
    }

    @Override
    public Long getEntityId() {
        return metadata.getEntityId(entity);
    }

}
