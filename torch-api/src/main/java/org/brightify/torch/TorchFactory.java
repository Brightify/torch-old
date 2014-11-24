package org.brightify.torch;

import org.brightify.torch.relation.RelationResolver;

/**
 * This class maintains the communication between the {@link DatabaseEngine} and actors. Instances of {@link Torch}
 * spawned by the factory will be able to operate with entities registered in this factory. If you need to have more
 * than one separate database, you can create your own factory. If you create more factories with the same {@link
 * DatabaseEngine} at the same time, the behavior is unknown at this moment.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TorchFactory {

    DatabaseEngine getDatabaseEngine();

    /**
     * Spawns new instance of {@link Torch}. You shouldn't keep the instance.
     *
     * @return New instance of Torch.
     */
    Torch begin();

    /**
     * Unloads this factory. Use this when this instance should get off scope.
     */
    void unload();

    Entities getEntities();

    /**
     * If the class represents an entity, this method tries to find relevant metadata class and recursively calls itself
     * with the new class. This recursive call is done to ensure the class is indeed an instance of {@link
     * EntityDescription}.
     *
     * If the class represents a metadata, this method instantiates the class and registers it. If you created your own
     * metadata class, ensure it has public no-arg constructor,
     *
     * NOTE: We recommend to use {@link TorchFactory#register(EntityDescription)} with an instance of metadata class in
     * production code as it's more efficient, because it doesn't use reflection.
     *
     * @param entityOrEntityMetadataClass Class of an entity, or of an entity metadata to be registered.
     * @param <ENTITY>                    Type of the target entity.
     *
     * @return Instance of the same factory.
     */
    <ENTITY> TorchFactory register(Class<ENTITY> entityOrEntityMetadataClass);

    /**
     * Registers passed metadata. You can use brightify-compiler to generate the {@link EntityDescription} or you can
     * make your own implementation.
     *
     * @param metadata Metadata to be registered.
     * @param <ENTITY> Type of the target entity.
     *
     * @return Instance of the same factory.
     */
    <ENTITY> TorchFactory register(EntityDescription<ENTITY> metadata);

    /**
     * @return An instance of a relation resolver.
     */
    RelationResolver getRelationResolver();

    public interface Configuration {

        void configureFactory(TorchFactory factory);
    }
}