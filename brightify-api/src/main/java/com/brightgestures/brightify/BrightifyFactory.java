package com.brightgestures.brightify;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface BrightifyFactory extends DatabaseEngine.OnCreateDatabaseListener {
    DatabaseEngine getDatabaseEngine();

    Brightify begin();

    SQLiteDatabase forceOpenOrCreateDatabase();

    boolean deleteDatabase();

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

    String getDatabaseName();

    /**
     * Sets database name. This method should be used only in {@link com.brightgestures.brightify
     * .FactoryConfiguration#configureFactory(BrightifyFactory)} as it's called before the factory is initialized.
     *
     * @param databaseName Desired database name.
     *
     * @throws IllegalStateException If factory has been already initialized (after constructor is run)
     * @throws java.lang.IllegalArgumentException If database name is empty or null
     */
    void setDatabaseName(String databaseName) throws IllegalStateException, IllegalArgumentException;
}