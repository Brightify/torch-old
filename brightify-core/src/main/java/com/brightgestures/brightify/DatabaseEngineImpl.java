package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
final class DatabaseEngineImpl implements DatabaseEngine {
    private static final String TAG = DatabaseEngineImpl.class.getSimpleName();
    private static final int DATABASE_CREATED_VERSION = 1001;

    private final Object mLock = new Object();
    private final Context mContext;
    private final String mDatabaseName;
    private final SQLiteDatabase.CursorFactory mCursorFactory;

    private SQLiteDatabase mDatabase;
    private boolean mInitializing;

    private OnConfigureDatabaseListener mOnConfigureDatabaseListener;
    private OnCreateDatabaseListener mOnCreateDatabaseListener;

    /**
     *
     * @param context
     * @param databaseName database name, if null then database will be only memory and deleted after closing
     * @param cursorFactory
     */
    public DatabaseEngineImpl(Context context, String databaseName, SQLiteDatabase.CursorFactory cursorFactory) {
        mContext = context;
        mDatabaseName = databaseName;
        mCursorFactory = cursorFactory;
    }

    @Override
    public String getDatabaseName() {
        return mDatabaseName;
    }

    @Override
    public void setOnConfigureDatabaseListener(OnConfigureDatabaseListener onConfigureDatabaseListener) {
        mOnConfigureDatabaseListener = onConfigureDatabaseListener;
    }

    @Override
    public void setOnCreateDatabaseListener(OnCreateDatabaseListener onCreateDatabaseListener) {
        mOnCreateDatabaseListener = onCreateDatabaseListener;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        synchronized (mLock) {
            if(mDatabase != null) {
                if(!mDatabase.isOpen()) {
                    // Database was closed from SQLiteDatabase#close
                    mDatabase = null;
                } else {
                    return mDatabase;
                }
            }

            if(mInitializing) {
                throw new IllegalStateException("Cannot create database while initializing!");
            }

            SQLiteDatabase db = null;
            try {
                mInitializing = true;

                if(mDatabaseName == null) {
                    db = SQLiteDatabase.create(mCursorFactory);
                } else {
                    db = mContext.openOrCreateDatabase(mDatabaseName, 0, mCursorFactory);
                }

                if(db.isReadOnly()) {
                    throw new SQLiteException("Database is in read-only mode, cannot proceed!");
                }

                if(mOnConfigureDatabaseListener != null) {
                    mOnConfigureDatabaseListener.onConfigureDatabase(db);
                }

                final int version = db.getVersion();
                if(version == 0) {
                    if(mOnCreateDatabaseListener != null) {
                        db.beginTransaction();
                        try {
                            mOnCreateDatabaseListener.onCreateDatabase(db);
                            db.setVersion(DATABASE_CREATED_VERSION);
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                        }
                    } else {
                        Log.w(TAG, "No OnCreateDatabaseListener is set! The database cannot be created!");
                    }
                }

                mDatabase = db;
                return db;
            } finally {
                mInitializing = false;
                if(db != null && db != mDatabase) {
                    db.close();
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (mLock) {
            if(mInitializing) {
                throw new IllegalStateException("Cannot close while initializing!");
            }

            if(mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
                mDatabase = null;
            }
        }
    }

    @Override
    public boolean deleteDatabase() {
        synchronized (mLock) {
            if(mInitializing) {
                throw new IllegalStateException("Cannot delete database while initializing!");
            }

            close();
            return mContext.deleteDatabase(mDatabaseName);
        }
    }

}
