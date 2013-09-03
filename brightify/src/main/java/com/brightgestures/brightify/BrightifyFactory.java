package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.NotNull;
import com.brightgestures.brightify.annotation.Unique;
import com.brightgestures.brightify.constraint.ColumnConstraint;
import com.brightgestures.brightify.sql.ColumnDef;
import com.brightgestures.brightify.sql.TypeName;
import com.brightgestures.brightify.sql.statement.CreateTable;
import com.brightgestures.brightify.sql.statement.DropTable;
import com.brightgestures.brightify.util.TypeUtils;

import java.util.Collection;

public class BrightifyFactory {

    private static String DEFAULT_DATABASE_NAME = "brightify_main_database";
    private static int DEFAULT_DATABASE_VERSION = 1;
    private static boolean DEFAULT_ENABLE_QUERY_LOGGING = false;

    protected String mDatabaseName = DEFAULT_DATABASE_NAME;
    protected int mDatabaseVersion = DEFAULT_DATABASE_VERSION;
    protected boolean mEnableQueryLogging = DEFAULT_ENABLE_QUERY_LOGGING;

    public BrightifyFactory() {
    }

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public void setDatabaseName(String databaseName) {
        mDatabaseName = databaseName;
    }

    public int getDatabaseVersion() {
        return mDatabaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        mDatabaseVersion = databaseVersion;
    }

    public boolean isEnableQueryLogging() {
        return mEnableQueryLogging;
    }

    public void setEnableQueryLogging(boolean enableQueryLogging) {
        mEnableQueryLogging = enableQueryLogging;
    }


    public Brightify begin(Context context) {
        return new Brightify(this, context);
    }

    public <T> void register(Class<T> entityClass) {
        EntityMetadata<T> metadata = new EntityMetadata<T>(entityClass);

        Entities.register(entityClass, metadata);
    }

    public void createDatabase(Context context) {
        Brightify brightify = BrightifyService.bfy(context);

        SQLiteDatabase db = brightify.getWritableDatabase();

        db.close();
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(mDatabaseName);
    }

    public void createTables(SQLiteDatabase db) {
        Collection<EntityMetadata<?>> metadataList = Entities.getAllMetadatas();

        for(EntityMetadata metadata : metadataList) {
            createTable(db, metadata);
        }
    }

    public <T> void createTable(SQLiteDatabase db, EntityMetadata<T> metadata) {
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

    public void dropTables(SQLiteDatabase db) {
        Collection<EntityMetadata<?>> metadataList = Entities.getAllMetadatas();

        for(EntityMetadata metadata : metadataList) {
            createTable(db, metadata);
        }
    }

    public <T> void dropTable(SQLiteDatabase db, EntityMetadata<T> metadata) {
        DropTable dropTable = new DropTable();
        dropTable.setTableName(metadata.getTableName());

        dropTable.run(db);
    }

}
