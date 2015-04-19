package org.brightify.torch;

import org.brightify.torch.filter.ReferenceProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class RefImpl<CHILD> implements Ref<CHILD> {
    private final EntityDescription<CHILD> description;
    private TorchFactory torchFactory;
    private Long idToLoad;

    private CHILD entity;

    public RefImpl(EntityDescription<CHILD> description, TorchFactory torchFactory, Long idToLoad) {
        this.description = description;
        this.torchFactory = torchFactory;
        this.idToLoad = idToLoad;
    }

    public RefImpl(EntityDescription<CHILD> description, CHILD entity) {
        this.description = description;
        this.entity = entity;
    }

    @Override
    public CHILD get() {
        if(idToLoad != null) {
            entity = torchFactory.begin().load().type(description.getEntityClass()).id(idToLoad);
            torchFactory = null;
            idToLoad = null;
        }
        return entity;
    }

    @Override
    public void set(CHILD entity) {
        /*
         * When user sets the entity from outside, we disable the load.
         */
        idToLoad = null;
        torchFactory = null;
        this.entity = entity;
    }

    @Override
    public boolean isLoaded() {
        return idToLoad != null;
    }

    @Override
    public EntityDescription<CHILD> getEntityDescription() {
        return description;
    }

    @Override
    public Long getEntityId() {
        if(entity == null) {
            return null;
        } else {
            return description.getIdProperty().get(entity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RefImpl<?> ref = (RefImpl<?>) o;

        return !(entity != null ? !entity.equals(ref.entity) : ref.entity != null);

    }

    @Override
    public int hashCode() {
        if(idToLoad != null) {
            return idToLoad.hashCode();
        }
        if(entity != null) {
            Long id = description.getIdProperty().get(entity);
            if(id != null) {
                return id.hashCode();
            }
        }

        return -1;
    }

    public static <ENTITY, CHILD> RefImpl<CHILD> of(ReferenceProperty<ENTITY, CHILD> property, TorchFactory torchFactory, Long childId) {
        return new RefImpl<CHILD>(property.getReferencedEntityDescription(), torchFactory, childId);
    }

}
