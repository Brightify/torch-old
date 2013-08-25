package com.brightgestures.droidorm.sql;

import android.database.sqlite.SQLiteDatabase;
import com.brainwashstudio.grease2d.engine.common.util.logging.Log;

public abstract class Statement implements IQueryable {

    @Override
    public String toString() {
        return toSQLString();
    }

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

    public void run(SQLiteDatabase db) {
        String sql = toSQLString();

        Log.d("Statement", sql);

        db.execSQL(sql);
    }
}
