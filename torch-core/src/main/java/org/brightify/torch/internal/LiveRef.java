package org.brightify.torch.internal;

import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Ref;
import org.brightify.torch.Torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LiveRef<ENTITY> implements Ref<ENTITY> {
    private final EntityMetadata<ENTITY> metadata;
    private final Class<ENTITY> entityClass;
    private final Long entityId;

    private Torch torch;

    private ENTITY value;
    private boolean loaded = false;

    public LiveRef(Class<ENTITY> entityClass, Long entityId, Torch torch) {
        metadata = torch.getFactory().getEntities().getMetadata(entityClass);
        this.entityClass = entityClass;
        this.entityId = entityId;
        this.torch = torch;
    }

    @Override
    public ENTITY get() {
        if (!loaded) {
            value = torch.load().type(entityClass).id(entityId);
            loaded = true;
        }
        return value;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
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
        return loaded ? metadata.getEntityId(value) : entityId;
    }

}
