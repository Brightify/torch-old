package com.brightgestures.droidorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.brightgestures.droidorm.action.Loader;
import com.brightgestures.droidorm.action.Saver;

public class Database extends SQLiteOpenHelper {

    protected final Context mContext;

    protected DatabaseFactory mFactory;

    protected Loader mLoader;
    protected Saver mSaver;

    public Database(DatabaseFactory factory, Context context) {
        super(context, factory.getDatabaseName(), null, factory.getDatabaseVersion());
        mContext = context;
        mFactory = factory;
    }

    public DatabaseFactory getFactory() {
        return mFactory;
    }

    public Loader load() {
        if(mLoader == null) {
            mLoader = new Loader(this);
        }
        return mLoader;
    }

    public Saver save() {
        if(mSaver == null) {
            mSaver = new Saver(this);
        }
        return mSaver;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mFactory.createTables(db);

        DatabaseService.setDatabaseCreated(mContext);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // implement migration/upgrade logic

    }
}
