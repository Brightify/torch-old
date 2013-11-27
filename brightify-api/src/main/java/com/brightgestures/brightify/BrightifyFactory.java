package com.brightgestures.brightify;

import android.database.sqlite.SQLiteDatabase;

/**
 * This class is maintaining the {@link com.brightgestures.brightify.DatabaseEngine} and registered entities. Instances
 * of {@link com.brightgestures.brightify.Brightify} spawned by the factory will be able to operate with entities
 * registered in this factory and database. If you need to have more than one separate database, you can create your own
 * factory. Each factory depends on the database name you specify through {@link com.brightgestures.brightify
 * .FactoryConfiguration}. If you create more factories with the same database name at the same time, the behavior is
 * unknown at this moment.
 *
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface BrightifyFactory extends DatabaseEngine.OnCreateDatabaseListener {

    DatabaseEngine getDatabaseEngine();

    /**
     * Spawns new instance of {@link com.brightgestures.brightify.Brightify}. You shouldn't keep the instance.
     * @return New instance of Brightify
     */
    Brightify begin();

    /**
     * Force creates the database.
     *
     * FIXME this is not needed anymore, as we always create the database on factory creation!
     *
     * @return
     */
    SQLiteDatabase forceOpenOrCreateDatabase();

    /**
     * Deletes the database which backs this factory.
     *
     * BEWARE: This will delete all the data of the database!
     * @return true if the deletion was successful, false otherwise.
     */
    boolean deleteDatabase();

    /**
     * Unloads this factory. Use this when this instance should get off scope.
     */
    void unload();

    Entities getEntities();

    /**
     * Finds metadata for the entity and registers it. On production you should use {@link com.brightgestures.brightify
     * .BrightifyFactory#register(EntityMetadata)} as it's more efficient, because it doesn't use reflection.
     *
     * @param entityClass Class of entity to be registered.
     * @param <ENTITY>    Type of entity.
     *
     * @return Instance of the same factory.
     */
    <ENTITY> BrightifyFactory register(Class<ENTITY> entityClass);

    /**
     * Registers passed metadata. You can use brightify-compiler to generate the {@link
     * com.brightgestures.brightify.EntityMetadata} or you can make your own implementation.
     *
     * @param metadata
     * @param <ENTITY>
     *
     * @return
     */
    <ENTITY> BrightifyFactory register(EntityMetadata<ENTITY> metadata);

    /**
     * Returns name of the database which backs this factory.
     *
     * @return database name
     */
    String getDatabaseName();

    /**
     * Sets database name. This method should be used only in {@link com.brightgestures.brightify
     * .FactoryConfiguration#configureFactory(BrightifyFactory)} as it's called before the factory is initialized.
     *
     * @param databaseName Desired database name.
     *
     * @throws IllegalStateException              If factory has been already initialized (after constructor is run)
     * @throws java.lang.IllegalArgumentException If database name is empty or null
     */
    void setDatabaseName(String databaseName) throws IllegalStateException, IllegalArgumentException;
}