package com.brightgestures.brightify;

import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.action.delete.Deleter;
import com.brightgestures.brightify.action.delete.DeleterImpl;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.LoaderImpl;
import com.brightgestures.brightify.action.save.Saver;
import com.brightgestures.brightify.action.save.SaverImpl;

public class BrightifyImpl implements Brightify {

    protected final BrightifyFactory mFactory;

    // TODO should we save mSaver and mLoader?

    public BrightifyImpl(BrightifyFactory factory) {
        mFactory = factory;
    }

    @Override
    public BrightifyFactory getFactory() {
        return mFactory;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        SQLiteDatabase db = mFactory.getDatabaseEngine().getDatabase();
        if(!db.isOpen()) {
            throw new IllegalStateException("Database is closed, should be opened!");
        }
        return db;
    }

    @Override
    public Deleter delete() {
        return new DeleterImpl(this);
    }

    @Override
    public Loader load() {
        return new LoaderImpl(this);
    }

    @Override
    public Saver save() {
        return new SaverImpl(this);
    }
}
