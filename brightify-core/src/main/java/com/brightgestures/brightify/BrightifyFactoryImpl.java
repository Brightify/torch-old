package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.NotNull;
import com.brightgestures.brightify.annotation.Unique;
import com.brightgestures.brightify.sql.constraint.ColumnConstraint;
import com.brightgestures.brightify.model.TableMetadata;
import com.brightgestures.brightify.sql.ColumnDef;
import com.brightgestures.brightify.sql.TypeName;
import com.brightgestures.brightify.sql.statement.CreateTable;
import com.brightgestures.brightify.sql.statement.DropTable;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.TypeUtilsCompatibility;

import java.util.Collection;

public class BrightifyFactoryImpl implements BrightifyFactory {

    protected final Context mContext;
    protected final FactoryConfiguration mConfiguration;
    protected final DatabaseEngine mDatabaseEngine;
    protected Handler mHandler;

    BrightifyFactoryImpl(Context applicationContext) {
        this(applicationContext, FactoryConfigurationImpl.create(applicationContext));
    }

    BrightifyFactoryImpl(Context applicationContext, FactoryConfiguration configuration) {
        mContext = applicationContext;
        mConfiguration = configuration;
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

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onCreateDatabase(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public <E> boolean asyncSuccessCallback(final Callback<E> callback, final E data) {
        return runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(data);
            }
        });
    }

    @Override
    public <E> boolean asyncFailedCallback(final Callback<E> callback, final Exception exception) {
        return runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(exception);
            }
        });
    }

    @Override
    public boolean runOnUiThread(Runnable runnable) {
        return mHandler.post(runnable);
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
            Class<? extends TypeName> affinityClass = TypeUtilsCompatibility.affinityFromClass(fieldType);

            TypeName affinity = TypeUtilsCompatibility.construct(affinityClass);
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
