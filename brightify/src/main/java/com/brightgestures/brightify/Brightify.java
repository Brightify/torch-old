package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import com.brightgestures.brightify.action.load.api.InitialLoader;
import com.brightgestures.brightify.action.load.impl.InitialLoaderImpl;
import com.brightgestures.brightify.action.save.Saver;
import com.brightgestures.brightify.util.Callback;

public class Brightify {

    protected final BrightifyFactory mFactory;

    // TODO should we save mSaver and mLoader?

    public Brightify(BrightifyFactory factory) {
        mFactory = factory;
    }

    public BrightifyFactory getFactory() {
        return mFactory;
    }

    /**
     * Shorthand for {@link DatabaseEngine#getDatabase()} from {@link BrightifyFactory#getDatabaseEngine()}.
     * @return ReadWrite database
     */
    public SQLiteDatabase getDatabase() {
        SQLiteDatabase db = mFactory.getDatabaseEngine().getDatabase();
        if(!db.isOpen()) {
            throw new IllegalStateException("Database is closed, should be opened!");
        }
        return db;
    }

    public InitialLoader load() {
        return new InitialLoaderImpl(this);
    }

    public Saver save() {
        return new Saver(this);
    }
}
