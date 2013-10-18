package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.NotNull;
import com.brightgestures.brightify.annotation.Unique;
import com.brightgestures.brightify.constraint.ColumnConstraint;
import com.brightgestures.brightify.model.TableMetadata;
import com.brightgestures.brightify.sql.ColumnDef;
import com.brightgestures.brightify.sql.TypeName;
import com.brightgestures.brightify.sql.statement.CreateTable;
import com.brightgestures.brightify.sql.statement.DropTable;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.TypeUtils;

import java.util.Collection;

public class BrightifyFactory implements EntityRegistrar, DatabaseEngine.OnCreateDatabaseListener {

    protected final Context mContext;
    protected final FactoryConfiguration mConfiguration;
    protected final DatabaseEngine mDatabaseEngine;
    protected Handler mHandler;

    BrightifyFactory(Context applicationContext) {
        this(applicationContext, FactoryConfiguration.create(applicationContext));
    }

    BrightifyFactory(Context applicationContext, FactoryConfiguration configuration) {
        mContext = applicationContext;
        mConfiguration = configuration;
        // TODO what about the CursorFactory (currently null = default)?
        mDatabaseEngine = new DatabaseEngine(applicationContext, configuration.getDatabaseName(), null);
        mDatabaseEngine.setOnCreateDatabaseListener(this);

        register(TableMetadata.class);
    }

    public FactoryConfiguration getConfiguration() {
        return mConfiguration;
    }

    public DatabaseEngine getDatabaseEngine() {
        return mDatabaseEngine;
    }

    public Brightify begin() {
        return new Brightify(this);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public <T> EntityRegistrar register(Class<T> entityClass) {
        EntityMetadataCompatibility<T> metadata = new EntityMetadataCompatibility<T>(entityClass);

        EntitiesCompatibility.register(entityClass, metadata);

        if(mConfiguration.isImmediateDatabaseCreation()) {
            forceOpenOrCreateDatabase();
        }

        return this;
    }

    @Override
    public <T> EntityMetadataCompatibility<T> unregister(Class<T> entityClass) {
        return EntitiesCompatibility.unregister(entityClass);
    }

    @Override
    public void onCreateDatabase(SQLiteDatabase db) {
        createTables(db);
    }

    public <E> boolean asyncSuccessCallback(final Callback<E> callback, final E data) {
        return runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(data);
            }
        });
    }

    public <E> boolean asyncFailedCallback(final Callback<E> callback, final Exception exception) {
        return runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(exception);
            }
        });
    }

    public boolean runOnUiThread(Runnable runnable) {
        return mHandler.post(runnable);
    }

    public SQLiteDatabase forceOpenOrCreateDatabase() {
        // This will force create the database
        return mDatabaseEngine.getDatabase();
    }

    public boolean deleteDatabase() {
        return mDatabaseEngine.deleteDatabase();
    }

    void unload() {
        EntitiesCompatibility.unregisterAll();

        mDatabaseEngine.close();
    }

    private void createTables(SQLiteDatabase db) {
        Collection<EntityMetadataCompatibility<?>> metadataList = EntitiesCompatibility.getAllMetadatas();

        for(EntityMetadataCompatibility metadata : metadataList) {
            createTable(db, metadata);
        }
    }

    private <T> void createTable(SQLiteDatabase db, EntityMetadataCompatibility<T> metadata) {
        CreateTable createTable = new CreateTable();
        createTable.setTableName(metadata.getTableName());
        for(Property property : metadata.getProperties()) {
            ColumnDef columnDef = new ColumnDef();
            columnDef.setName(property.getColumnName());

            Class<?> fieldType = property.getType();
            Class<? extends TypeName> affinityClass = TypeUtils.affinityFromClass(fieldType);

            TypeName affinity = TypeUtils.construct(affinityClass);
            columnDef.setTypeName(affinity);

            if(property.isAnnotationPresent(Id.class)) {
                Id id = property.getAnnotation(Id.class);

                ColumnConstraint.PrimaryKey primaryKey = new ColumnConstraint.PrimaryKey();
                primaryKey.setAutoIncrement(id.autoIncrement());
                primaryKey.setColumnName(columnDef.getName());
                columnDef.addColumnConstraint(primaryKey);
            }
            if(property.isAnnotationPresent(NotNull.class)) {
                ColumnConstraint.NotNull notNull = new ColumnConstraint.NotNull();
                notNull.setColumnName(columnDef.getName());
                columnDef.addColumnConstraint(notNull);
            }
            if(property.isAnnotationPresent(Unique.class)) {
                ColumnConstraint.Unique unique = new ColumnConstraint.Unique();
                unique.setColumnName(columnDef.getName());
                columnDef.addColumnConstraint(unique);
            }

            createTable.addColumnDef(columnDef);
        }

        createTable.run(db);
    }

    private void dropTables(SQLiteDatabase db) {
        Collection<EntityMetadataCompatibility<?>> metadataList = EntitiesCompatibility.getAllMetadatas();

        for(EntityMetadataCompatibility metadata : metadataList) {
            createTable(db, metadata);
        }
    }

    private <T> void dropTable(SQLiteDatabase db, EntityMetadataCompatibility<T> metadata) {
        DropTable dropTable = new DropTable();
        dropTable.setTableName(metadata.getTableName());

        dropTable.run(db);
    }

}
