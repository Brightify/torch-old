package org.brightify.torch.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.Settings;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.action.load.OrderLoader;
import org.brightify.torch.android.internal.SQLiteMaster;
import org.brightify.torch.android.internal.SQLiteMaster$;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.filter.Property;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.PropertyUtil;
import org.brightify.torch.util.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AndroidSQLiteEngine implements DatabaseEngine {
    private static final String TAG = AndroidSQLiteEngine.class.getSimpleName();
    private static final int DATABASE_CREATED_VERSION = 1001;
    private static Map<Class<?>, String> typeAffinities = new HashMap<Class<?>, String>();

    static {
        Class<?>[] integerTypes = {
                Boolean.class,
                boolean.class,
                Byte.class,
                byte.class,
                Short.class,
                short.class,
                Integer.class,
                int.class,
                Long.class,
                long.class,
        };
        addAffinityTypes("INTEGER", integerTypes);

        Class<?>[] realTypes = {
                Float.class,
                float.class,
                Double.class,
                double.class,
        };
        addAffinityTypes("REAL", realTypes);

        Class<?>[] numericTypes = { };
        addAffinityTypes("NUMERIC", numericTypes);

        Class<?>[] textTypes = {
                String.class,
        };
        addAffinityTypes("TEXT", textTypes);
    }

    private final Object lock = new Object();
    private final Context context;
    private final String databaseName;
    private final SQLiteDatabase.CursorFactory cursorFactory;
    private final Map<Class<?>, CompiledStatement> compiledStatements = new HashMap<Class<?>, CompiledStatement>();
    private final SQLiteMaster$ sqLiteMasterMetadata = SQLiteMaster$.create();
    private TorchFactory torchFactory;
    private SQLiteDatabase database;
    private boolean initializing;

    /**
     * @param databaseName database name, if null then database will be only in memory and deleted after closing
     */
    public AndroidSQLiteEngine(Context context, String databaseName, SQLiteDatabase.CursorFactory cursorFactory) {
        this.context = context.getApplicationContext();
        this.databaseName = databaseName;
        this.cursorFactory = cursorFactory;
    }

    private static String affinityFor(Class<?> cls) {
        if (typeAffinities.containsKey(cls)) {
            return typeAffinities.get(cls);
        }
        return "NONE";
    }

    private static void addAffinityTypes(String affinity, Class<?>... classes) {
        for (Class<?> cls : classes) {
            typeAffinities.put(cls, affinity);
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public SQLiteDatabase getDatabase() {
        synchronized (lock) {
            if (database != null) {
                if (!database.isOpen()) {
                    // Database was closed source SQLiteDatabase#close
                    database = null;
                } else {
                    return database;
                }
            }

            if (initializing) {
                throw new IllegalStateException("Cannot create database while initializing!");
            }

            SQLiteDatabase db = null;
            try {
                initializing = true;

                if (databaseName == null) {
                    db = SQLiteDatabase.create(cursorFactory);
                } else {
                    db = context.openOrCreateDatabase(databaseName, 0, cursorFactory);
                }

                if (db.isReadOnly()) {
                    throw new SQLiteException("Database is in read-only mode, cannot proceed!");
                }

                // CONFIGURE THE DATABASE

                //final int version = db.version();
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
                if (db != null && db != database) {
                    db.close();
                }
            }
        }
    }

    public void close() {
        synchronized (lock) {
            if (initializing) {
                throw new IllegalStateException("Cannot close while initializing!");
            }

            if (database != null && database.isOpen()) {
                database.close();
                database = null;
            }
        }
    }

    public boolean deleteDatabase() {
        synchronized (lock) {
            if (initializing) {
                throw new IllegalStateException("Cannot delete database while initializing!");
            }

            close();
            return context.deleteDatabase(databaseName);
        }
    }

    @Override
    public <ENTITY> List<ENTITY> load(LoadQuery<ENTITY> query) {
        CursorIterator<ENTITY> iterator = new CursorIterator<ENTITY>(torchFactory, query, runQuery(query, false));

        List<ENTITY> result = new ArrayList<ENTITY>();

        while(iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
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
    public <ENTITY> Map<ENTITY, Long> save(Iterable<ENTITY> entities) {
        Map<ENTITY, Long> results = new HashMap<ENTITY, Long>();
        Iterator<ENTITY> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return results;
        }
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            @SuppressWarnings("unchecked")
            Class<ENTITY> entityClass = (Class<ENTITY>) iterator.next().getClass();
            EntityDescription<ENTITY> description = torchFactory.getEntities().getDescription(entityClass);
            Validate.notNull(description, "Entity not registered! Be sure to register it into the factory!");

            CompiledStatement statement = precompileInsertStatement(description);
            DirectBindWritableRawEntity rawEntity = new DirectBindWritableRawEntity(statement);

            for (ENTITY entity : entities) {
                description.toRawEntity(torchFactory, entity, rawEntity);

                long entityId = statement.getSQLiteStatement().executeInsert();

                if (entityId == -1) {
                    // FIXME should we continue in the save or discard it completely?
                    throw new IllegalStateException("Error when storing data into database!");
                }

                description.setEntityId(entity, entityId);

                results.put(entity, entityId);
            }

            db.setTransactionSuccessful();

            return results;
        } catch (Exception e) {
            // FIXME handle the exception better
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public <ENTITY> Map<ENTITY, Boolean> delete(Iterable<ENTITY> entities) {
        SQLiteDatabase db = getDatabase();

        db.beginTransaction();
        try {
            Map<ENTITY, Boolean> results = new HashMap<ENTITY, Boolean>();
            EntityDescription<ENTITY> metadata = null;
            for (ENTITY entity : entities) {
                if (metadata == null) {
                    @SuppressWarnings("unchecked")
                    Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
                    metadata = torchFactory.getEntities().getDescription(entityClass);
                }

                int affected = db.delete(metadata.getSafeName(), metadata.getIdProperty().getSafeName() + " = ?",
                                         new String[] { String.valueOf(metadata.getEntityId(entity)) });
                if (affected > 1) {
                    throw new IllegalStateException("Delete command affected more than one row at once!");
                }

                results.put(entity, affected == 1);
            }

            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityDescription<ENTITY> metadata) {
        return new AndroidSQLiteMigrationAssistant<ENTITY>(this, metadata);
    }

    @Override
    public TorchFactory getTorchFactory() {
        return torchFactory;
    }

    @Override
    public void setTorchFactory(TorchFactory factory) {
        torchFactory = factory;
        if (factory != null) {
            factory.getEntities().registerMetadata(SQLiteMaster$.create());
        }
    }

    @Override
    public boolean wipe() {
        return deleteDatabase();
    }

    protected <ENTITY> CompiledStatement precompileInsertStatement(EntityDescription<ENTITY> metadata) {
        if (compiledStatements.containsKey(metadata.getEntityClass())) {
            return compiledStatements.get(metadata.getEntityClass());
        }

        Property<?>[] properties = metadata.getProperties();

        Map<String, Integer> bindArgIndexes = new HashMap<String, Integer>();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT OR REPLACE INTO ").append(metadata.getSafeName()).append(" (");

        int propertyCount = properties.length;
        for (int i = 0; i < propertyCount; i++) {
            if (i > 0) {
                sql.append(',');
            }
            String propertyName = properties[i].getSafeName();
            sql.append(propertyName);
            // i + 1 because bind arguments start with 1 and not 0
            bindArgIndexes.put(propertyName, i + 1);
        }
        sql.append(") VALUES (");
        for (int i = 0; i < propertyCount; i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append('?');
        }
        sql.append(')');

        String builtSql = sql.toString();

        logSql(builtSql, new String[0]);

        CompiledStatement compiledStatement =
                new CompiledStatement(getDatabase().compileStatement(builtSql), bindArgIndexes);
        compiledStatements.put(metadata.getEntityClass(), compiledStatement);
        return compiledStatement;
    }

    protected <ENTITY> void createTableIfNotExists(EntityDescription<ENTITY> description) {

        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS ");
        // append(databaseName).append('.').
        sql.append(description.getSafeName()).append(" (");

        Property<?>[] properties = description.getProperties();
        int i = 0;
        for (Property<?> property : properties) {
            if (i++ > 0) {
                sql.append(',');
            }

            Id.IdFeature idFeature = PropertyUtil.getFeature(property, Id.IdFeature.class);

            sql.append(property.getSafeName()).append(" ").append(affinityFor(property.getType()));

            if (idFeature != null) {
                sql.append(" CONSTRAINT ")
                   .append(property.getSafeName())
                   .append("_primary")
                   .append(" PRIMARY KEY ASC ");

                if (idFeature.isAutoIncrement()) {
                    sql.append("AUTOINCREMENT ");
                }
            }
        }
        sql.append(")");

        execSql(sql.toString(), new String[0]);
    }

    protected <ENTITY> void dropTableIfExists(EntityDescription<ENTITY> description) {
        execSql("DROP TABLE IF EXISTS ?", new String[] { description.getSafeName() });
    }

    protected <ENTITY> boolean tableExists(EntityDescription<ENTITY> description) {
        return torchFactory.begin()
                           .load()
                           .type(SQLiteMaster.class)
                           .filter(SQLiteMaster$.tableName.equalTo(description.getSafeName())).count() > 0;
    }

    private <ENTITY> Cursor runQuery(LoadQuery<ENTITY> query, boolean countOnly) {
        StringBuilder builder = new StringBuilder();

        List<String> selectionArgsList = new ArrayList<String>();

        if (countOnly) {
            builder.append("SELECT count(1)");
        } else {
            builder.append("SELECT ");

            Property<?>[] properties = query.getEntityDescription().getProperties();

            // TODO add some validation of filters
            int i = 0;
            for (Property<?> property : properties) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append(property.getSafeName());
                i++;
            }
        }

        builder.append(" FROM ").append(query.getEntityDescription().getSafeName());

        if (query.getFilter() != null) {
            builder.append(" WHERE ");

            SQLiteWhereClauseBuilder.appendFilter(builder, query.getFilter(), selectionArgsList);
        }

        if (query.getOrderMap().size() > 0) {
            builder.append(" ORDER BY ");
            int i = 0;
            for (Map.Entry<Property<?>, OrderLoader.Direction> entry : query.getOrderMap().entrySet()) {
                if (i++ > 0) {
                    builder.append(',');
                }
                builder.append(entry.getKey().getSafeName()).append(" ")
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

    private void execSql(String sql, Object[] bindArgs) {
        logSql(sql, bindArgs);

        getDatabase().execSQL(sql, bindArgs);
    }

    private Cursor runSql(String sql, List<String> selectionArgsList) {
        String[] selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);

        logSql(sql, selectionArgs);

        return getDatabase().rawQuery(sql, selectionArgs);
    }

    private void logSql(String sql, Object[] selectionArgs) {
        if (Settings.isQueryLoggingEnabled()) {
            if (Settings.isQueryArgumentsLoggingEnabled()) {
                sql = sql + " with arguments: " + Arrays.deepToString(selectionArgs);
            }

            if (Settings.isStackTraceQueryLoggingEnabled()) {
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

    protected static class CompiledStatement {

        private final SQLiteStatement statement;
        private final Map<String, Integer> bindArgIndexes;

        CompiledStatement(SQLiteStatement statement, Map<String, Integer> bindArgIndexes) {
            this.statement = statement;
            this.bindArgIndexes = bindArgIndexes;
        }

        public SQLiteStatement getSQLiteStatement() {
            return statement;
        }

        public Map<String, Integer> getBindArgIndexes() {
            return bindArgIndexes;
        }
    }
}
