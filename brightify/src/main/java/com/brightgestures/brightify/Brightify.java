package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.impl.InitialLoaderImpl;
import com.brightgestures.brightify.action.save.Saver;

public class Brightify extends SQLiteOpenHelper {

    protected final Context mContext;

    protected BrightifyFactory mFactory;

    // TODO should we save mSaver and mLoader?

    public Brightify(BrightifyFactory factory, Context context) {
        super(context, factory.getDatabaseName(), null, factory.getDatabaseVersion());
        mContext = context;
        mFactory = factory;
    }

    public BrightifyFactory getFactory() {
        return mFactory;
    }

    public Loader load() {
        return new InitialLoaderImpl(this);
    }

    public Saver save() {
        return new Saver(this);
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
