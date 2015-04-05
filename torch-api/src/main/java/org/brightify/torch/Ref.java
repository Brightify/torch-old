package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Ref<CHILD> {

    CHILD get();

    void set(CHILD entity);

    boolean isLoaded();

    EntityDescription<CHILD> getEntityDescription();

    Long getEntityId();

}
