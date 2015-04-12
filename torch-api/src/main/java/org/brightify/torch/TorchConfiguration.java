package org.brightify.torch;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TorchConfiguration<CONFIGURATION extends TorchConfiguration<CONFIGURATION>> {

    Set<EntityDescription<?>> getRegisteredEntityDescriptions();

    DatabaseEngine getDatabaseEngine();

    /**
     * If the class represents an entity, this method tries to find relevant description class and recursively calls
     * itself with the found class. This recursive call is done to ensure the class is indeed an instance of {@link
     * EntityDescription}.
     *
     * If the class represents a description, this method instantiates the class and registers it. If you created your
     * own description class, ensure it has public no-arg constructor, or register it using {@link
     * #register(EntityDescription)}.
     *
     * NOTE: We recommend to use {@link #register(EntityDescription)} with an instance of the description class in
     * production code as it is more efficient, because it does not use reflection for instantiation.
     *
     * @param entityOrEntityDescriptionClass Class of an entity, or of an entity description to be registered.
     *
     * @return Instance of the same factory.
     */
    CONFIGURATION register(Class<?> entityOrEntityDescriptionClass);

    /**
     * Registers passed description. You can use torch-compiler to generate the {@link EntityDescription} or you can
     * make your own implementation.
     *
     * @param description Metadata to be registered.
     * @param <ENTITY> Type of the target entity.
     *
     * @return Instance of the same factory.
     */
    <ENTITY> CONFIGURATION register(EntityDescription<ENTITY> description);

    CONFIGURATION register(Collection<? extends EntityDescription<?>> descriptions);

    CONFIGURATION register(EntityDescription<?>... descriptions);

    TorchFactory initializeFactory();

}
