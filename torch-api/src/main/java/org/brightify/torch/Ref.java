package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Ref<ENTITY> {

    ENTITY get();

    boolean isLoaded();

    boolean isSaved();

    Class<ENTITY> getEntityClass();

    Long getEntityId();

}
