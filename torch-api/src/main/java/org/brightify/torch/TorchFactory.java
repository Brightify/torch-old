package org.brightify.torch;

/**
 * This class is maintaining the {@link DatabaseEngine} and registered entities. Instances of {@link Torch} spawned by
 * the factory will be able to operate with entities registered in this factory and database. If you need to have more
 * than one separate database, you can create your own factory. Each factory depends on the database name you specify
 * through {@link org.brightify.torch.FactoryConfiguration}. If you create more factories with the same database name at
 * the same time, the behavior is unknown at this moment.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TorchFactory {

    DatabaseEngine getDatabaseEngine();

    /**
     * Spawns new instance of {@link Torch}. You shouldn't keep the instance.
     *
     * @return New instance of Torch
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
     * EntityMetadata}.
     * <p/>
     * If the class represents a metadata, this method instantiates the class and registers it. If you created your own
     * metadata class, ensure it has public no-arg constructor,
     * <p/>
     * NOTE: We recommend to use {@link TorchFactory#register(EntityMetadata)} with an instance of metadata class in
     * production code as it's more efficient, because it doesn't use reflection.
     *
     * @param entityOrEntityMetadataClass Class of an entity, or of an entity metadata to be registered.
     * @param <ENTITY>                    Type of entity.
     *
     * @return Instance of the same factory.
     */
    <ENTITY> TorchFactory register(Class<ENTITY> entityOrEntityMetadataClass);

    /**
     * Registers passed metadata. You can use brightify-compiler to generate the {@link EntityMetadata} or you can make
     * your own implementation.
     */
    <ENTITY> TorchFactory register(EntityMetadata<ENTITY> metadata);

    /**
     * Returns an instance of relation resolver.
     */
    RelationResolver getRelationResolver();
    
    public interface Configuration {

        void configureFactory(TorchFactory factory);
    }
}