package org.brightify.torch;

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
        return description.getIdProperty().get(entity);
    }


}
