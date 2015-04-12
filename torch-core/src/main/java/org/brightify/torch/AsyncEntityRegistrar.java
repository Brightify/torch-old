package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncEntityRegistrar {
    AsyncEntityRegistrarSubmit register(Class<?> entityClass);

    <ENTITY> AsyncEntityRegistrarSubmit register(EntityDescription<ENTITY> metadata);
}
