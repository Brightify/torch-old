package com.brightgestures.droidorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.droidorm.annotation.Id;
import com.brightgestures.droidorm.annotation.NotNull;
import com.brightgestures.droidorm.annotation.Unique;
import com.brightgestures.droidorm.constraint.ColumnConstraint;
import com.brightgestures.droidorm.sql.ColumnDef;
import com.brightgestures.droidorm.sql.TypeName;
import com.brightgestures.droidorm.sql.statement.CreateTable;
import com.brightgestures.droidorm.util.TypeUtils;

import java.util.Collection;

public class DatabaseFactory {

    protected String mDatabaseName;
    protected int mDatabaseVersion;
    protected boolean mEnableQueryLogging;

    public DatabaseFactory(String databaseName, int databaseVersion, boolean enableQueryLogging) {
        mDatabaseName = databaseName;
        mDatabaseVersion = databaseVersion;
        mEnableQueryLogging = enableQueryLogging;
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


    public Database begin(Context context) {
        return new Database(this, context);
    }

    public <T> void register(Class<T> entityClass) {
        EntityMetadata<T> metadata = new EntityMetadata<T>(entityClass);

        Entities.register(entityClass, metadata);
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


}
