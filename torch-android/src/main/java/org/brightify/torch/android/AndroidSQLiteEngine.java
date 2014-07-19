package org.brightify.torch.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.RawEntity;
import org.brightify.torch.Settings;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.filter.Column;
import org.brightify.torch.filter.EntityFilter;
import org.brightify.torch.util.MigrationAssistant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class AndroidSQLiteEngine implements DatabaseEngine {
    private static final String TAG = AndroidSQLiteEngine.class.getSimpleName();
    private static final int DATABASE_CREATED_VERSION = 1001;

    private final Object lock = new Object();
    private final Context context;
    private final String databaseName;
    private final SQLiteDatabase.CursorFactory cursorFactory;

    private TorchFactory torchFactory;

    private SQLiteDatabase database;
    private boolean initializing;

    private final Map<>

    /**
     *
     * @param context
     * @param databaseName database name, if null then database will be only memory and deleted after closing
     * @param cursorFactory
     */
    public AndroidSQLiteEngine(Context context, String databaseName, SQLiteDatabase.CursorFactory cursorFactory) {
        this.context = context;
        this.databaseName = databaseName;
        this.cursorFactory = cursorFactory;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public SQLiteDatabase getDatabase() {
        synchronized (lock) {
            if(database != null) {
                if(!database.isOpen()) {
                    // Database was closed source SQLiteDatabase#close
                    database = null;
                } else {
                    return database;
                }
            }

            if(initializing) {
                throw new IllegalStateException("Cannot create database while initializing!");
            }

            SQLiteDatabase db = null;
            try {
                initializing = true;

                if(databaseName == null) {
                    db = SQLiteDatabase.create(cursorFactory);
                } else {
                    db = context.openOrCreateDatabase(databaseName, 0, cursorFactory);
                }

                if(db.isReadOnly()) {
                    throw new SQLiteException("Database is in read-only mode, cannot proceed!");
                }

                // CONFIGURE THE DATABASE

                //final int version = db.getVersion();
                //if(version == 0) {
                //    if(torchFactory != null) {
                //        db.beginTransaction();
                //        try {
                //            mOnCreateDatabaseListener.onCreateDatabase(db);
                //            db.setVersion(DATABASE_CREATED_VERSION);
                //            db.setTransactionSuccessful();
                //        } finally {
                //            db.endTransaction();
                //        }
                //    } else {
                //        Log.w(TAG, "No OnCreateDatabaseListener is set! The database cannot be created!");
                //    }
                //}

                database = db;
                return db;
            } finally {
                initializing = false;
                if(db != null && db != database) {
                    db.close();
                }
            }
        }
    }

    public void close() {
        synchronized (lock) {
            if(initializing) {
                throw new IllegalStateException("Cannot close while initializing!");
            }

            if(database != null && database.isOpen()) {
                database.close();
                database = null;
            }
        }
    }

    public boolean deleteDatabase() {
        synchronized (lock) {
            if(initializing) {
                throw new IllegalStateException("Cannot delete database while initializing!");
            }

            close();
            return context.deleteDatabase(databaseName);
        }
    }

    @Override
    public <ENTITY> Iterator<ENTITY> load(LoadQuery<ENTITY> query) {
        return new CursorIterator<ENTITY>(query.getEntityMetadata(), runQuery(query, false));
    }

    @Override
    public <ENTITY> int count(LoadQuery<ENTITY> loadQuery) {
        Cursor cursor = runQuery(loadQuery, true);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, ENTITY> save(Iterable<ENTITY> entities) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            Map<Key<ENTITY>, ENTITY> results = new HashMap<Key<ENTITY>, ENTITY>();
            Class<ENTITY> entityClass = (Class<ENTITY>) entities.iterator().next().getClass();
            EntityMetadata<ENTITY> metadata = torchFactory.getEntities().getMetadata(entityClass);
            if (metadata == null) {
                throw new IllegalStateException("Entity not registered!");
            }

            for (ENTITY entity : entities) {
                RawEntity rawEntity = null;
                try {
                    metadata.toRawEntity(entity, rawEntity);
                } catch (Exception e) {
                    // FIXME handle the exception better
                    throw new RuntimeException(e);
                }
                long id = db.replaceOrThrow(metadata.getTableName(), null, values);


                StringBuilder sql = new StringBuilder();
                sql.append("INSERT OR REPLACE INTO (");

                sql.append(") VALUES (");
                sql.append()
                sql.append(")");
                database.compileStatement(sql.toString());

                Object[] bindArgs = null;
                int size = (initialValues != null && initialValues.size() > 0)
                           ? initialValues.size() : 0;
                if (size > 0) {
                    bindArgs = new Object[size];
                    int i = 0;
                    for (String colName : initialValues.keySet()) {
                        sql.append((i > 0) ? "," : "");
                        sql.append(colName);
                        bindArgs[i++] = initialValues.get(colName);
                    }
                    sql.append(')');
                    sql.append(" VALUES (");
                    for (i = 0; i < size; i++) {
                        sql.append((i > 0) ? ",?" : "?");
                    }
                } else {
                    sql.append(nullColumnHack + ") VALUES (NULL");
                }
                sql.append(')');


                SQLiteStatement sqLiteStatement = database.compileStatement(sql.toString());
                sqLiteStatement.bind

                SQLiteStatement statement = new SQLiteStatement(this, sql.toString(), bindArgs);
                try {
                    return statement.executeInsert();
                } finally {
                    statement.close();
                }


                if (id == -1) {
                    throw new IllegalStateException("Error when storing data into database!");
                }

                metadata.setEntityId(entity, id);

                Key<ENTITY> key = KeyFactory.create(entityClass, id);

                results.put(key, entity);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private <ENTITY> SQLiteStatement prepareSaveStatement(RawEntity rawEntity) {

    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> delete(Iterable<Key<ENTITY>> keys) {
        SQLiteDatabase db = getDatabase();

        db.beginTransaction();
        try {
            Map<Key<ENTITY>, Boolean> results = new HashMap<Key<ENTITY>, Boolean>();
            EntityMetadata<ENTITY> metadata = null;
            for (Key<ENTITY> key : keys) {
                if (metadata == null) {
                    metadata = torchFactory.getEntities().getMetadata(key.getType());
                }

                int affected = db.delete(metadata.getTableName(), metadata.getIdColumn().getName() + " = ?",
                                         new String[] { String.valueOf(key.getId()) });
                if (affected > 1) {
                    throw new IllegalStateException("Delete command affected more than one row at once!");
                }

                results.put(key, affected == 1);
            }

            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityMetadata<ENTITY> metadata) {
        return new AndroidSQLiteMigrationAssistant<ENTITY>(this, metadata);
    }

    @Override
    public void setTorchFactory(TorchFactory factory) {
        torchFactory = factory;
    }

    @Override
    public boolean wipe() {
        return deleteDatabase();
    }

    private <ENTITY> Cursor runQuery(LoadQuery<ENTITY> query, boolean countOnly) {
        StringBuilder builder = new StringBuilder();

        LinkedList<String> selectionArgsList = new LinkedList<String>();

        if(countOnly) {
            builder.append("SELECT count(1)");
        } else {
            builder.append("SELECT ");

            String[] columns = query.getEntityMetadata().getColumns();

            // TODO add some validation of filters
            int i = 0;
            for (String column : columns) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(column);
                i++;
            }
        }

        builder.append(" FROM ").append(query.getEntityMetadata().getTableName());

        if (query.getEntityFilters().size() > 0) {
            builder.append(" WHERE ");
            for (EntityFilter filter : query.getEntityFilters()) {
                filter.toSQL(selectionArgsList, builder);
            }
        }

        if (query.getOrderMap().size() > 0) {
            builder.append(" ORDER BY ");
            for (Map.Entry<Column<?>, OrderLoader.Direction> entry : query.getOrderMap().entrySet()) {
                builder.append(entry.getKey().getName()).append(" ")
                       .append(entry.getValue() == OrderLoader.Direction.ASCENDING ? "ASC" : "DESC");
            }
        }

        if (query.getLimit() != null) {
            builder.append(" LIMIT ").append(query.getLimit());
            if (query.getOffset() != null) {
                builder.append(" OFFSET ").append(query.getOffset());
            }
        }

        builder.append(";");

        return runSql(builder.toString(), selectionArgsList);
    }

    private Cursor runSql(String sql, List<String> selectionArgsList) {
        String[] selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);

        logSql(sql, selectionArgs);

        return database.rawQuery(sql, selectionArgs);
    }

    private void logSql(String sql, String[] selectionArgs) {
        if(Settings.isQueryLoggingEnabled()) {
            if(Settings.isQueryArgumentsLoggingEnabled()) {
                sql = sql + " with arguments: " + Arrays.deepToString(selectionArgs);
            }

            if(Settings.isStackTraceQueryLoggingEnabled()) {
                try {
                    throw new Exception(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, sql);
            }
        }
    }
}
