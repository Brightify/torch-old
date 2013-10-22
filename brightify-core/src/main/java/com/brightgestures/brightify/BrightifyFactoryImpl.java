package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.model.TableMetadataMetadata;
import com.brightgestures.brightify.util.AsyncRunner;
import com.brightgestures.brightify.util.Callback;

import java.util.Collection;

public class BrightifyFactoryImpl implements BrightifyFactory {

    protected final Context mContext;
    protected final FactoryConfiguration mConfiguration;
    protected final DatabaseEngine mDatabaseEngine;

    BrightifyFactoryImpl(Context applicationContext) {
        this(applicationContext, FactoryConfigurationImpl.create(applicationContext));
    }

    BrightifyFactoryImpl(Context applicationContext, FactoryConfiguration configuration) {
        mContext = applicationContext;
        mConfiguration = configuration;
        // We need to register our metadata
        Entities.registerMetadata(new TableMetadataMetadata());
        // TODO what about the CursorFactory (currently null = default)?
        mDatabaseEngine = new DatabaseEngineImpl(applicationContext, configuration.getDatabaseName(), null);
        mDatabaseEngine.setOnCreateDatabaseListener(this);
    }

    @Override
    public FactoryConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return mDatabaseEngine;
    }

    @Override
    public Brightify begin() {
        return new BrightifyImpl(this);
    }

    public <ENTITY> void register(EntityMetadata<ENTITY> metadata) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void onCreateDatabase(SQLiteDatabase db) {
        Collection<EntityMetadata<?>> metadatas = Entities.getAllMetadatas();

        for(EntityMetadata<?> metadata : metadatas) {
            metadata.createTable(db);
        }
    }

    @Override
    public SQLiteDatabase forceOpenOrCreateDatabase() {
        // This will force create the database
        return mDatabaseEngine.getDatabase();
    }

    @Override
    public boolean deleteDatabase() {
        return mDatabaseEngine.deleteDatabase();
    }

    @Override
    public void unload() {
        EntitiesCompatibility.unregisterAll();

        mDatabaseEngine.close();
    }

}
