package com.brightgestures.brightify;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface DatabaseEngine {
    String getDatabaseName();

    void setOnConfigureDatabaseListener(OnConfigureDatabaseListener onConfigureDatabaseListener);

    void setOnCreateDatabaseListener(OnCreateDatabaseListener onCreateDatabaseListener);

    SQLiteDatabase getDatabase();

    void close();

    boolean deleteDatabase();

    public interface OnConfigureDatabaseListener {
        void onConfigureDatabase(SQLiteDatabase db);
    }

    public interface OnCreateDatabaseListener {
        void onCreateDatabase(SQLiteDatabase db);
    }
}
