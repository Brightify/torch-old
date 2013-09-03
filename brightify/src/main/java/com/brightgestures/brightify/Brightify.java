package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.brightgestures.brightify.action.Loader;
import com.brightgestures.brightify.action.Saver;

public class Brightify extends SQLiteOpenHelper {

    protected final Context mContext;

    protected BrightifyFactory mFactory;

    protected Loader mLoader;
    protected Saver mSaver;

    public Brightify(BrightifyFactory factory, Context context) {
        super(context, factory.getDatabaseName(), null, factory.getDatabaseVersion());
        mContext = context;
        mFactory = factory;
    }

    public BrightifyFactory getFactory() {
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

        BrightifyService.setDatabaseCreated(mContext);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        BrightifyService.setDatabaseNotCreated(mContext);

        mFactory.dropTables(db);

        onCreate(db);
    }
}
